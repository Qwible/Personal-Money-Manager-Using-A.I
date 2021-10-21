-----------------------Viewing and running the app code -----------------------
It is reccomended to open the BankingApp project folder in Android Studio so that the structure is shown clearly. 

Using Android Studio the app can be run in an emulated device using the AVD manager as described here - https://developer.android.com/studio/run/emulator.
This requires Intel HAXM, and therefore must be done on an intel processor. 

Alternatively an Android Device can be connected via USB and the app can be run on this using USB debugging - https://developer.android.com/studio/run/device.

Finally the app-debug.apk file can be opened on an Android Device to install the app. 
On most phones you will need to enable the “Allow Installation from Unknown Sources” option. 
You should hopefully get prompted to turn the option on when opening the file, if not try this 
- https://www.technipages.com/where-did-allow-installation-from-unknown-sources-go-in-android


----------------------Viewing and running on the regression code--------------------
regression_model.ipynb contains the code for building and training the prediction model.
It is reccomended that this is opened using google colaboratory - https://colab.research.google.com/notebooks/intro.ipynb to avoid dependancy errors.

The model is trained and tested by running each cell in order. Cell 2 will prompt a file upload - select the export.csv file.

