# Quad Map

Simple android app to control Quad over socket connection.

<img src="https://github.com/ash-anand/QuadMap-android/raw/master/Screenshots/Screenshot_20170409-233557.png" alt="Drawing" style="width: 300px;"/> 
<img src="https://github.com/ash-anand/QuadMap-android/raw/master/Screenshots/Screenshot_20170409-233641.png" alt="Drawing" style="width: 300px;"/>

It uses Google Maps API to retrive GPS points and display markers with quad position.

## Modes

### Single Point Follow

Specified as Mode A in [onboardScripts](https://github.com/ash-anand/onboardScripts) 

A single marker will appear to tap. On tapping the marker you will see the GPS coordinates to the marker as well as an info saying Tap to send quad. Upon tapping on the info a string is sent to the quad with mode A tag.

<img src="https://github.com/ash-anand/QuadMap-android/raw/master/Screenshots/Screenshot_20170409-233632.png" alt="Drawing" style="width: 300px;"/>

### Path Following Mode

Specified as Mode B in [onboardScripts](https://github.com/ash-anand/onboardScripts) 

Multiple markers can be placed on the map. Each marker will be numbered and a line will be drawn showing the path to be  travelled by the quad. Upon on tapping any marker you will see the info which on tapping will send the string of GPS points to quad with mode B tag.


<img src="https://github.com/ash-anand/QuadMap-android/raw/master/Screenshots/Screenshot_20170409-233736.png" alt="Drawing" style="width: 300px;"/>

## Other Features

* Apart from selecting two modes you can also see the current location of the quad once the GPS signal is locked on quad.


*  A default height can be set for the quad if "Custom Height" is unchecked in menu


* If "Custom Height" is checked you will be asked to enter height for each marker you place.


* In case of emergency there is a green floating button which will switch the quad to land mode.


* Home icon in menu will tell quad to come to your current location.

<img src="https://github.com/ash-anand/QuadMap-android/raw/master/Screenshots/Screenshot_20170409-233647.png" alt="Drawing" style="width: 300px;"/>
<img src="https://github.com/ash-anand/QuadMap-android/raw/master/Screenshots/Screenshot_20170409-233708.png" alt="Drawing" style="width: 300px;"/><img src="https://github.com/ash-anand/QuadMap-android/raw/master/Screenshots/Screenshot_20170409-233619.png" alt="Drawing" style="width: 300px;"/>
