# Program Breakdown of Image Processing

***

## How to Use the Program

There are three different ways this program can be used and viewed.

1) Interactively - One can use the program interactively with the GUI view if they wish to visually
   see what is going on as they press the button commands. They can do this by running the jar file
   with the flag `-interactive`. How to use the GUI is documented in the USEME.md file.

2) Using a File Script - When one opens the jar file they can use the `-script` flag and pass in a
   file will all commands they wish to run. Commands are below.

3) Writing Commands - You can also input commands directly by using the `-text` flag when running
   the jar file. Commands for this are below.

---
Commands:

* `load image (STRING => Image FileName)` loads the image at the file path into a new layer. The
  current layer is not updated, but adds the image as a new layer.

* `load state (STRING => Layer FileName)` loads the layer at the file path.

* `apply (STRING => Name of modifier)` applies this modifier to the image set at current. Modifiers
  include ("blur", "sharpen", "sepia", and "greyscale").

* `apply mosaic (INTEGER => seed)` applies the modifier mosaic to the image set at current. The
  integer represents the number of seeds you want for this mosaic.

* `apply downscale (INTEGER => width) (INTEGER => height)` applies downscale the entire layer. All
  images will be downsized to the input width and height which must be lower than the current width
  and height.

* `set (INTEGER => Layer index)` sets this index of the layer (counting from 1) as the current
  image.

* `toggle (INTEGER => Layer index)` toggles the visibility of the image on that layer index again
  counting from 1. If the image is currently visible, it will be made invisible and vice versa.

* `save state (STRING => Filename)` saves the current state of the layers to a txt file that can be
  loaded later. No path or extension should be given just file name it will save automatically to
  the res/ folder as a .txt file.

* `save image INTEGER (STRING => Filename)` saves the image at the integer index to the file.
  Filename should be given with path and image extension type.

* `save image current (STRING => Filename)` saves the current image to the file. Filename should be
  given with path and image extension type.

* `export (STRING => Filename)` saves the layer to an image file. Filename should be given with path
  and image extension type.

* `exit` to exit the program.

---

### Complete Components of the program

* All models are complete for the current image extensions. If any need to be added in the future,
  the interfaces and classes are open for extension.
* Reading and writing image files is also currently complete unless any other image implementations
  wish to be added.
* The controllers for the current GUI and text versions of the file are also complete.

---

### Class Mosaic implements IModifier

The Mosaic class has one constructor which takes in a number of seeds. The mosaic class uses the
k-means clustering algorithm in order to create an image that is the mosaic version of the original.

Methods:

* `toString()` - returns "mosaic"
* `modify()` - returns updated list of pixels that has the mosaic scheme
* `sowSeeds()` - randomly generates clusters based on the number of seeds
* `findDistance()` - finds the distance between two pixels
* `clamp()` - finds centroids within a certain x and y range of the pixel
* `getClosestCentroid()` - finds the closest centroid to the input pixel
* `cluster()` - finds the closest centroid for each pixel
* `getAverage()` - finds the average color in a centroid

---

### Class DownScale implements IModifier 

This class is represents a filter that modifies an image to be smaller than what it currently is
based on an input height and width by the user. It has a constructor that takes in these two
parameters for this very purpose.

Methods:

* `toString()` - returns "downscale"
* `modify()` - returns updated list of pixels that represents a smaller version of the current
  IImage if the input width and height are less than the current width and height.

---
Final Additions
1. DownScale - We made this into a class that implements IModifier. This in itself worked but there
   was one problem, we were unable to update the width and height of the IImage and ILayer to match
   the new size. To fix this problem, we added a method to the IImage interface changeCanvasSize
   that takes in a width and height and allows you to set a new width and height that are less than
   the current height and width. Also in ILayer, we created a method alterLayer which took in the
   IModifier, width and height, and applied the IModifier to every IImage in the ILayer. We also
   updated the width and height of the ILayer at the end if the width and height were less than the
   current.

2. Mosaic - Mosaic was much simpler as all we needed to do was create a class that implemented
   IModifier. We used the k-means clustering algorithm for this. The only issue with this that we
   had was that the bigger the image and number of seeds, the more time it would take to update the
   image.

3. All in all, these additions had to implement IModifier rather than extend one of the abstract
   classes because they lacked kernels that the AModifier abstract class defined.

---

### Changes to initial design

1. We first changed any public or default access modifiers and created observer methods in order to
   prevent changes to be made to the existing data outside the classes.
2. We also removed any I/O in IImage so that it would be separate and only in the controller. This
   way we have no overlapping coupling of the controller and model.
3. We also created interface IPixel in order to allow future implementation of it that may be
   different.
4. We extended Checkerboard from Image instead of implementing IImage because there was a lot of
   overlap in the methods.
5. We also created a file controller that replaced imageUtils to handle I/O.
6. In order to allow for jpg,jpeg,and png files to be exported we had to create a new method in
   IImage that allows the creation of a Buffered image from the current IImage.
7. Also, we had to add changeCanvasSize in order to create the downscaling option for an IImage. In
   ILayer, we added a getCurrent method which would allow a user to get the image at the current
   index.
8. The IFileController was updated to incorporate the reading and writing of layer text files as
   well as jpg,jpeg, and png image files. This was directly implemented into FileController.
9. One change we had to make because of the GUI was the created of a Read-Only Layer interface. This
   interface outlined observer methods that the GUI view would be able to use as we could not always
   delegate these from the controller. The ILayer interface extended the IROLayer and added a few
   methods but did not change the overall functionality of the Layer class.

---

# Citations

Flower Image:
Libert, Christophe. “Free Sunflowers 6 Stock Photo.” FreeImages,
www.freeimages.com/photo/sunflowers-6-1392951.

Road Image:
Libert, Christophe. “Free Road to nowhere Stock Photo” FreeImages,
https://www.freeimages.com/photo/road-to-nowhere-1383109

Land Image: Romashkin, Maksim. “Ridge against Icy Sea under Blue Sky.” Pexels, 11 Mar. 2021,
www.pexels.com/photo/ridge-against-icy-sea-under-blue-sky-7108213/. 
