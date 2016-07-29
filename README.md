Mundane MoneyDance Extension
===

## Development instructions

### MoneyDance development kit

If on a POSIX system (linux, unix, Mac OS X), execute the following from the root of this project. It downloads and
places the extracted files at the right place.

```
curl http://infinitekind-downloads.s3.amazonaws.com/moneydance-devkit-4.0.tar.gz | tar -zxv
```

Otherwise, do it manually:
- Download the MoneyDance development kit following this link: [moneydance-devkit-4.0](http://infinitekind-downloads.s3.amazonaws.com/moneydance-devkit-4.0.tar.gz). The direct link may change, so refer to [Moneydance developper section](http://infinitekind.com/developer) on their website.
- Extract it at the root of this project under the same name (without the extension)


### Generate keys

Simple execute this and enter a pass phrase you will remember. It will be needed everytime you build the extension. 

```
ant genkeys
```

### Build and package the extension

Execute ```ant``` without argument will build & package the extension.

```
ant
```

The result is the ```mxt``` file that can be found in ```dist```.


## Develop with IntelliJ IDEA

NB: This was written for IntelliJ IDEA 2016.1.3.

### Getting started

#### Create the project and set up dependencies

- Open the root folder of the project in IntelliJ.
- Menu `File` -> `Project Structure`
    - Section `Project Settings` -> `Project`
        - Set `Project compiler output` to `{path to the project}/build` (use the `...` button to help you)
    - Section `Project Settings` -> `Libraries`
        - Click the `+` button (2nd column) and chose `Java`
        - Browse to the installation folder of Moneydance and select the sub-folder with all the JAR files in. Typically, this will be:
            - on a Mac: `/Applications/Moneydance.app/Contents/Java`
            - on Windows: `C:\Program Files\Moneydance\jars`
            - on Linux: well ... wherever you installed it.
        - Click `OK`
        - In the `Choose Module` window appears, select `Mundane` and click `OK`
        - Back in the `Project Structure` window, rename the newly added library to `Moneydance Installed` and click "OK" (bottom right).

#### Select the source folder

- In the `Project` pane (usually on the left), right-click on folder `src`
- In `Mark directory as`, select `Sources root`

### Debugging and running directly from the IDE

Taken from a [forum post](http://help.infinitekind.com/discussions/moneydance-development/824-debugging-moneydance-extensions-in-eclipse) at infinitekind.

#### In a nutshell

Add a Run/Debug Configuration of type `Application` with the following settings:

- `Main class`: `Moneydance`
- `Working directory`:
    - on Windows: `C:\Program Files\Moneydance\jars`
    - on Mac OS X: `/Applications/Moneydance.app/Contents/Java`

The first time, you need to:

- build the extension from the command line (with `ant`)
- start the application from IntelliJ (run or debug)
- install the extension (the generated `.mxt` file, in folder `dist` of the project)

The next times, the modifications will automatically be taken into account when started from IntelliJ. No need to build and install the extension at every change.


## Note on document folder and configuration files

### Mac OS X

Configuration file when launched normally, from the Moneydance Mac OS X ".app":

    ~/Library/Containers/com.infinitekind.MoneydanceOSX/Data/Library/Application Support/Moneydance

Configuration file when launched manually (ie: from IntelliJ in 'debug'):

    ~/Library/Application Support/Moneydance

In both cases, the configuration file is `config.dict`. This facilitates the development since
there can be 2 sets of settings, default data file and extensions. You will not mess with your
normal files and configuration while developing.


## Original README from Infinite Kind


### Description

This is a toolkit that will assist in the creation of extensions
for the Moneydance personal finance application.  Extensions can
be compiled, packaged, and signed using the ANT build tool.  ANT
is open source, and is increasingly used to replace Makefiles 
when building Java projects.  ANT can be downloaded for many
platforms from http://ant.apache.org/

The Moneydance Developers Kit includes an ANT build.xml, the necessary
jar files to compile and build an extension, and working sample source
code for a new extension.  API documentation for Moneydance can be
found in the "Developer" section of http://infinitekind.com/moneydance


### Usage

Before building your extension, you will first need to generate a
key pair.  This can be done by running "ant genkeys" from the "src"
directory.  You will be prompted for a passphrase that is used to
encrypt the private key file.  Your new keys will be stored in the
priv_key and pub_key files.

Once your keys have been generated, you are ready to compile an 
extension.  The build.xml file has been set up to compile and
build the sample extension with ID "myextension".  The source 
code for this sample extension can be found under:

```
    src/com/moneydance/modules/features/myextension/
```

To compile and package the sample extension, run "ant myextension"
from the src directory.  After the extension is compiled and built,
you will be asked for the passphrase to your private key which will
be used to sign the extension and place the new extension file in
the dist directory with the name myextension.mxt.  Please feel free 
to modify the source to the "myextension" extension to build your own 
extensions.

If you would like to share your extension with others and would prefer
they not see the unrecognized-signature warning when loading the extension
then you can send your source code to support@moneydance.com where we
will inspect the source code for security problems, compile the
extension, and sign it with the official moneydance key.  If you like,
we can then also put the extension into the list of available extensions
for all Moneydance users to see.


### Advanced Usage

To create your own extension that is separate from the sample 
extension you must first come up with a unique ID for your extension.  
An extension ID is all lower case and alphanumeric.  For this example, 
let's say your new extension ID is "newextension".  You would take the 
following steps to set up the development environment for the new extension:

1) Copy the files from ```src/com/moneydance/modules/features/myextension/``` to ```src/com/moneydance/modules/features/newextension/```

2) Edit the new java source files to change the package names from ```com.moneydance.modules.features.myextension``` to ```com.moneydance.modules.features.newextension```

   When loading an extension, looks for the class named: ```com.moneydance.modules.features.{extensionid}.Main```
   
3) Add a "newextension" target to the build.xml file.  This can be
   done easily by duplicating the "myextension" target in the
   build.xml file, and changing every instance of "myextension" to
   "newextension" in the new target.
   
4) At this point you can run "ant newextension" in the src directory
   and your new extension will be built and placed in the dist
   directory.


### Further Assistance

If you would like further assisstance, please contact support@moneydance.com
We will be more than happy to answer any questions.

