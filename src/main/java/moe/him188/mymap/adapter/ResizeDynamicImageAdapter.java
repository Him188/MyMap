package moe.him188.mymap.adapter;

import cn.nukkit.Server;
import cn.nukkit.scheduler.AsyncTask;
import moe.him188.mymap.MyMap;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Objects;

/**
 * @author Him188 @ MyMap Project
 */
public class ResizeDynamicImageAdapter extends ImageAdapter {
    private static final int CACHE_SIZE = 50;
    private static final int DEFAULT_DELAY = 3000;

    private int n;

    private int cachedKey;
    private LinkedList<SingleImageAdapter> cachedImages;
    private int[] delays;

    private File imageDirectory;
    private File[] files;

    private int adaptationWidth;
    private int adaptationHeight;
    private int maxIndex;

    public ResizeDynamicImageAdapter(File file) {
        setImage(file);
    }

    public void setImage(File imageDirectory) {
        this.imageDirectory = Objects.requireNonNull(imageDirectory, "imageDirectory");
        if (!imageDirectory.isDirectory()) {
            throw new IllegalArgumentException("file is not directory");
        }
    }

    public File getImageDirectory() {
        return this.imageDirectory;
    }

    @Override
    public synchronized void doAdaptation(int width, int height) throws IOException {
        this.adaptationWidth = width;
        this.adaptationHeight = height;

        this.files = this.imageDirectory.listFiles();
        if (this.files == null) {
            this.cachedKey = 0;
            this.cachedImages = new LinkedList<>();
            this.delays = new int[0];
            return;
        }

        this.cachedKey = 0;
        this.cachedImages = new LinkedList<>();
        this.delays = new int[this.files.length];

        for (int i = 0; i < this.files.length; i++) {
            this.delays[i] = getDelay(this.files[i]);
        }

        this.n = 0;
        this.updateCache(CACHE_SIZE);
    }

    private static int getDelay(File file) {
        String name = Objects.requireNonNull(file).getName();
        if (name.contains(".")) {
            name = name.substring(0, name.indexOf("."));
        }

        String[] strings;
        if (name.contains("-")) {
            strings = name.split("-");
        } else if (name.contains("_")) {
            strings = name.split("_");
        } else {
            return DEFAULT_DELAY;
        }

        try {
            return (int) Double.parseDouble(strings[1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {
            return DEFAULT_DELAY;
        }
    }

    private synchronized void updateCache(int count) throws IOException {
        if (count < this.files.length) {
            maxIndex = count - 1;
            for (int i = 0; i < count; i++) {
                if (this.cachedKey++ == this.files.length) {
                    this.cachedKey = 0;
                }
                this.cachedImages.poll();
                SingleImageAdapter subAdapter = new ResizeImageAdapter(this.files[this.cachedKey]);
                subAdapter.doAdaptation(adaptationWidth, adaptationHeight);
                this.cachedImages.offer(subAdapter);
            }
        } else {
            maxIndex = this.files.length - 1;
            if (this.cachedImages.isEmpty()) {
                for (int i = 0; i < this.files.length; i++) {
                    SingleImageAdapter subAdapter = new ResizeImageAdapter(this.files[this.cachedKey + i]);
                    subAdapter.doAdaptation(adaptationWidth, adaptationHeight);
                    this.cachedImages.addLast(subAdapter);
                }
            }
        }
    }

    public int getFrameCount() {
        return this.files.length;
    }

    public SingleImageAdapter getNextFrame() {
        return getCachedFrame(this.cachedKey + this.n);
    }

    public synchronized SingleImageAdapter getCachedFrame(int i) {
        return this.cachedImages.get(i);
    }

    public int getNextDelay() {
        return this.getCachedDelay(this.n);
    }

    public int getDelay(int i) {
        return this.delays[i];
    }

    public int getCachedDelay(int i) {
        return this.delays[this.cachedKey + i];
    }

    public void next() {
        if (this.n++ == maxIndex) {
            this.n = 0;
            Server.getInstance().getScheduler().scheduleAsyncTask(MyMap.getInstance(), new AsyncTask() {
                @Override
                public void onRun() {
                    try {
                        updateCache(CACHE_SIZE);
                    } catch (IOException e) {
                        MyMap.getInstance().getLogger().error("加载图片时遇到错误", e);
                    }
                }
            });
        }
    }
}
