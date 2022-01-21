# USEME for interactive GUI in jar file

###BUTTONS:

Image Modifiers:

* `Blur`
    * applies Blur to the image set at current.

* `Sharpen`
    * applies Sharpen to the image set at current.

* `Sepia`
    * applies Sepia to the image set at current.

* `Greyscale`
    * applies Greyscale to the image set at current.

* `DownScale`
    * applies Downscale to the entire layer.
    * asks user to input integers for width and height to the size they wish to downscale to.

* `Mosaic`
    * applies Mosaic to the image set at current.
    * asks the user to input an integer for the number of seeds they wish to apply to the mosaic.

Layer Tools:

* `Set Current`
    * sets the input integer index of the layer (counting from 1) as the current image.

* `Toggle Visibility`
    * toggles the visibility of the image on inputted layer index again counting from 1.
    * if the image is currently visible, it will be made invisible and vice versa.

* `Blend All Layers`
    * blends all layers that are set to visible into one image that will appear in the view.

Other Features:

* `Load`
    * loads an image at the file chosen into a new layer. The current layer is not updated, but adds
      the image as a new layer which is placed behind the current one.
    * can also load txt layer files.

* `Save`
    * saves the current state of the layers to a txt file that can be loaded later. This file can be
      place in any folder and will have the .txt extension.
    * the file name should be inputted in the 'File Name' box of the file chooser.

* `Export`
    * creates an image file for the current image.
    * in order to export layers as an image, one must hit the Blend All Layers first.
    * the image extension can be chosen in the drop-down menu 'Files of Type:'.
    * possible extensions include "jpg", "jpeg", "png", and "ppm".
  
