	**********************
	*  READ ME CARDRIVING 2D VERSION 1.0.16
	*
	*  by Piazza Francesco Giovanni 
	*  Tecnes Milano ,Italy http://www.tecnes.com ; Tel.: +39.02.67101036
	*	
	**********************
		
	Java version:1.6.0_17
	Edited using Eclipse Platform Version 3.5.1
	************
	*
	* REFERENCE BOOK :
	* Developing Games In Java - Book
	* By David Brackeen, Bret Barker, Laurence Vanhelsuwé 
	* 
	*  
	*********************
	*
	* to launch from command line extract the lib directory from the jar 
	* in the same directory and write :
	* java -classpath Driving2D.jar    com.CarFrame2D
	*
    * (you can also simply double click on the jar file to run the main program
    *   after extracting the lib)
    *
	*
	* to launch the road editor use the command:
	*
	* java -classpath Driving2D.jar    com.RoadEditor
	*********************
	
	To accelerate: up arrow key.
	TO brake:down arrow key
	Forward/rear mode: press A or S
	To steer left,right:left arrow key,right arrow key.
	Horn :press H key.
	To restart : press the button "Reset car".
	
	You can add your personal car going to the lib directory and 
	adding an image file named supercar_NUMBER_OF_IMAGE.gif ,where NUMBER_OF_IMAGE
	is an integer starting from 1... :keep in mind,the back ground must be 
	white !Then to change car press key "c".
	
	You can also add your personal background going to the lib directory and 
	adding an image file named background_NUMBER_OF_IMAGE.gif ,where NUMBER_OF_IMAGE
	is an integer starting from 1... :keep in mind,the back ground color must be 
	primary blue !Then to change background press key "b".
	
	You can also add your personal object going to the lib directory and 
	adding an image file named object_NUMBER_OF_IMAGE.gif ,where NUMBER_OF_IMAGE
	is an integer starting from 1... :keep in mind,the back ground color must be 
	white !
	
	
	*************************
	
	---ROAD FORMAT:
	
	Is a succession of linear sections  :
	
	Header:
	#NX=number of points
    #NY=INTEGER_NUMBER number of sections to compose the road.
	
	Rows:
	X0,Y0,Z0_X1,Y1,Z1,HEXCOLOR,NUMTEXTURE_ ... repeated points
	
	where HEXCOLOR is a string rgb color in the hex representation ,e.g. for white :FFFFFF.
	and NUMTEXTURE is the index number of the texture associated
	
	---OBJECTS FORMAT:
	
	No header.
	Rows:
	
	X_Y_Z_DX_DY_DZ_OBJECT-INDEX_HEXCOLOR
	
	where HEXCOLOR is a string rgb color in the hex representation ,e.g. for white :FFFFFF.
	
	For the 2D object the value DY is never used,but it must be written =0 !
	
	if you want to use textures create a file driving.properties in the lib directory and
	write in:
	ISUSETEXTURE=true

	*************

	ROAD EDITOR

   Road and objects are loaded and saved separately in the load and save menu.
   
   Select object and road parts ,then to modify them type new coordinates and press 
   "Change Point".

   Editor short keys:

        a : add new row
        d : delete last row
        i: insert object
        b: change selected object 
        p: change selected point
        e : deselect all
        f1:zoom in
        f2 zoom out
        
    To insert an object press the right button of the mouse in the point where you want the 
    object,otherwise the button "insert object" to have it in a fixed starting point,or type the coordinates
    of the new object and then press the button "insert object".
    
    If you dont'want to clean up some text boxes after every insertion to reuse their values then check 
    the case at the right of them.
    
    Press,drag and release the mouse to select an area of road points.

 To move objects and road points use also the panel with the arrows at the cardinal points,
    putting in the central text field the quantity by which you want to move them.
	
	