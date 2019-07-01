# MyMap

MyMap plugin for [Nukkit](https://github.com/nukkit/nukkit). Show your gif image on the server!

## Description

This plugin allows you to show static images, gif images, multi images
on your server.  
Screenshots are in the end.

## Download
- [TeamCity](http://mamoe.net:2333/viewType.html?buildTypeId=Mymap_Build)

## Usage

1. Install the plugin into your server. And join in.
2. Create a rectangle in a world(Any one, not just the main world).
3. Use command `/mymap add <id>` to make the rectangle become a frame.
4. Put your images into `plugins/MyMap/images/`.
5. Use command `/mymap setpicture <id> <imageFileName>` to set the picture
   in the frame. (imageFileName not include path)
6. The plugin will start to fill in the frame. Enjoy it!

## Tips

- If you want to change the picture, just use command `/mymap setpicture <id> <imageFile>` again.
- An OP can long-touch(Left-click for Win10) a frame to look up id of the frame.
- This plugin supports languages in 简体中文(Chinese Simplified) and English
- This plugin will auto select the language(based on your system language)
- You can change language in config.yml

## Q&A

- How to set GIF image?  
  The same way as static images.
- How to set Multi images?  
  Create a folder under plugins/MyMap/images, put images in it.(Needs static picture, dynamic GIF only displays the first frame).  
  Name of picture decides how long to display. (Not included the suffix, explodes by "-", the last one is the display time)  
  Examples:
  - pic-3000.jpg  displays 3000ms = 3s
  - img-500.png  displays 500ms = 0.5s
  - -3000.bmp  displays 3000ms = 3s

  PS: If filename has no or more than 1 "-", or the number is invalid(negative), it will use default display time 3000ms=3s.

## Screenshots

![](images/MyMap-GIF.gif)  
![](images/MyMap-STATIC.jpg)
