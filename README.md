Mundane MoneyDance Extension
===

## Development instructions

### Moneydance development kit

If on a POSIX system (linux, unix, Mac OS X), execute the following from the root of this project. It downloads and
places the extracted files at the right place.

    curl http://infinitekind-downloads.s3.amazonaws.com/moneydance-devkit-4.0.tar.gz | tar -zxv

Otherwise, do it manually:
- Download the Moneydance development kit following this link: [moneydance-devkit-4.0](http://infinitekind-downloads.s3.amazonaws.com/moneydance-devkit-4.0.tar.gz). The direct link may change, so refer to [Moneydance developper section](http://infinitekind.com/developer) on their website.
- Extract it at the root of this project under the same name (without the extension)

### Generate keys

Simple execute this and enter a pass phrase you will remember. It will be needed every time you build the extension. 

    ant genkeys

### Build and package the extension

Execute ```ant``` without argument will build & package the extension.

    ant

The result is the ```mxt``` file that can be found alongside the initial
assembled jar (ie: ```target/```).

This actually performs two things. It first calls ```sbt assembly```, 
which compiles the projects, executes the tests and pack the fat-jar
(including all dependencies). It then calls ```ant sign``` to sign the
resulting JAR into a ```.mxt``` file, ready to be installed in
Moneydance as an extension.


## Develop with IntelliJ IDEA

NB: This was written for IntelliJ IDEA 2016.1.3.

### Getting started

#### Create the project and set up dependencies

- Open the root folder of the project in IntelliJ.
- Menu `File` -> `Project Structure`
    - Section `Project Settings` -> `Libraries`
        - Click the `+` button (2nd column) and chose `Java`
        - Browse to the installation folder of Moneydance and select the sub-folder with all the JAR files in. Typically, this will be:
            - on a Mac: `/Applications/Moneydance.app/Contents/Java`
            - on Windows: `C:\Program Files\Moneydance\jars`
            - on Linux: well ... wherever you installed it.
        - Add the documentation, using the `+` button at the bottom of the right section:
        	- From the project root: `moneydance-devkit-4.0/doc`
        	- This should create a `JavaDocs` section below `Classes`.
        - Rename the newly added library to `Moneydance Installed` and click "OK" (bottom right).


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

Please do read the README provided with the Moneydance Extension
development kit: ```moneydance-devkit-4.0/README.txt```
