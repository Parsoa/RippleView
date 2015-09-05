# RippleView
Android Library for implementing the Material design ripple effect on pre-lollipop devices

the library is not ye available on neither Maven or Gradle but will soon be up and running . 
for the time being add the downloaded project as an external library in android studio or Eclipse .

## Usage 

Using the RippleView is pretty simple . there are three overloads of the static method _DrawRippleAtPosition_ to handle different use scenarios :

### DrawRippleAtPosition(Activity context , View targetView , int colorID , ViewGroup rootLayout)

_context_ : you need to pass a context in order to let the view inflate itself andaccess resources. pass the current activity (or if inside 
a fragment the result of getActivity() )

targetView : you probably want the ripple to appear on another view , for instance a button or TextView . adding the ripple on view
will cause to align its center with that of the target view .

_colorID_ : the resource identifier for the ripple color . pass something like R.color.ColorPrimaryDark .

_rootLayout_ : the root Layout of the activity or fragment currently in the foreground. pass the top ViewGroup in your layout hierarchy.

### DrawRippleAtPosition(Activity context , int rippleCenterX , int rippleCenterY , int colorID , ViewGroup rootLayout)

_rippleCenterX , rippleCenterY_ : just in case you want the ripple to appear at some arbitrary position in the rootlayout call it this way.

other parameters are as previous . 

### DrawRippleAtPosition(Activity context , int rippleCenterX , int rippleCenterY ,int rippleRadius , int colorID , ViewGroup rootLayout)
 
 just like the previous overload but you can also set the radius of the ripple . by default it would be 80px .


