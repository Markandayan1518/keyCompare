# keyCompare
Compare Properties file using Keys

To Configuration 
-------------------

In order to use keyCompare JAR, you must do the following steps:

JAR requires "conf.properties" file in current directory  

Ensure update values of conf.properties as your needs.

PATH          ->  Location of the Properties files to compare  [ eg: PATH=...\ManageEngine\DesktopCentral_Server\lib\resources]

FILENAMES     ->  Base file name, suppose you have more than one base file separated them with Comma ',' [eg: FILENAMES=ApplicationResources, JSApplicationResources]

OUTPUTFOLDER  ->  Comparison reports will generate in following output folder as per filename. [eg: OUTPUTFOLDER=KeyDiff]  

OUTPUTFORMAT  ->  Generated output file based on this format. [eg: OUTPUTFORMAT=html]


Note : Don't use Quotation mark while assigning values

-----------------------------------------------------------------

Example: conf.properties 
-------------------------

PATH=C:/ManageEngine/DesktopCentral_Server/lib/resources

FILENAMES=ApplicationResources, JSApplicationResources

OUTPUTFOLDER=KeyDiff

OUTPUTFORMAT=html

---------------------------------------------------------



To  Run
----------

open command prompt same location where keyCompare.jar is located.

Type cmd :  java -jar keyCompare.jar config.properties

{or}

run.bat


Output Results 
--------------

Kindly open generated output text files in notepad++
