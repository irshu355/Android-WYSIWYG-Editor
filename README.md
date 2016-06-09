Laser-Native-Editor
===================

**Laser-Native-Editor** is an iFrame free WYSIWYG editor that uses the native components in the content tree.

Download
------------
gradle:

    compile 'com.github.irshulx:laser-native-editor:0.1.0'

or maven:

    <dependency>
      <groupId>com.github.irshulx</groupId>
      <artifactId>laser-native-editor</artifactId>
      <version>0.1.0</version>
      <type>pom</type>
    </dependency>

Demo
--------------

![enter image description here](https://raw.githubusercontent.com/irshuLx/laser-native-editor/master/screens/gif-1.gif)&nbsp;&nbsp;&nbsp;&nbsp;![enter image description here](https://raw.githubusercontent.com/irshuLx/laser-native-editor/master/screens/gif-2.gif)&nbsp;&nbsp;&nbsp;&nbsp;![enter image description here](https://raw.githubusercontent.com/irshuLx/laser-native-editor/master/screens/gif-3.gif)&nbsp;&nbsp;&nbsp;&nbsp;![enter image description here](https://scontent-kul1-1.xx.fbcdn.net/v/t1.0-9/13227159_269389593411500_6219375228375154304_n.jpg?oh=f6b5fe0ab000d0eb89915d0147ff24ef&oe=579B3038)&nbsp;&nbsp;&nbsp; ![enter image description here](https://scontent-kul1-1.xx.fbcdn.net/v/t1.0-9/13177651_269390020078124_2120913121972781573_n.jpg?oh=4a94f50c352330007063dc7dd092da0d&oe=57E6CE25)  


Features
-------------

 - **Renderer or Editor**: You can use Laser as a **Renderer** to Render the content or use it as an **Editor** to create the content.
 - **No Webviews used** to render the content. Laser uses Native EditText, ImageView and as such to render the contents.
 - **HTML Parser:** Render your HTML Code into the editor and vice versa.
 - **Image Uploader Api:** Use the Built-in API to upload the images to the server.
 - Flexibility to create your own Toolbar layout. Just hookup the API methods on the click events.

The editor is built, so that every part of the design have been exposed and is available for customization. You can define, how the editor should look like, and what are the controls, that should be available (the controls toolbar layout can also be created by yourself, just call the API methods on the click event).

**Available Controls:**

| Control     | Usage |
| :------- | :-----: |
| `H1`,  `H2` and `H3` | Insert Headings | 
| `Bold`, `Italic`, `Intent` & `Outdent`    | Format the text   | 
| `Image Picker`| Insert Images to the editor from storage or a URL    | 
| `Hyperlinks` | Add Links to the editor
|`Location Selector` | Use the embedded map editor to tag and insert locations to the editor |
|`Numbered` and `Bulleted` Lists | Let's you created Unorderd and Ordered lists |
|`Line Divider` | Add a divider among paragraphs or Headings
|`Clear Content` | Remove all contents from the editor



Usage
-------------------

**Layout XML**

  

    <com.github.irshulx.Editor
        android:layout_width="match_parent"
        android:id="@+id/editor"
        app:render_type="Editor"
        app:placeholder="Start writing here..."
        android:paddingTop="10dp"
        android:paddingLeft="10dp"
        android:paddingRight="10dp"
        android:layout_height="wrap_content"
        android:paddingBottom="100dp"
    />

**Activity**


	 

 
     @Override
     protected void onCreate(Bundle savedInstanceState) {

        _editor= (Editor) findViewById(R.id.editor);
        _editor.Render();
        
        // below are the API's you can use to insert content into the editor
        
        findViewById(R.id.action_header_1).setOnClickListener(new View.OnClickListener()   {
            @Override
            public void onClick(View v) {
                _editor.UpdateTextStyle(ControlStyles.H1);
            }
        });
        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _editor.UpdateTextStyle(ControlStyles.BOLD);
            }
        });

        findViewById(R.id.action_Italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _editor.UpdateTextStyle(ControlStyles.ITALIC);
            }
        });
        findViewById(R.id.action_bulleted).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _editor.InsertList(false);
            }
        });
        findViewById(R.id.action_unordered_numbered).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _editor.InsertList(true);
            }
        });
        findViewById(R.id.action_hr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _editor.InsertDivider();
            }
        });
        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _editor.OpenImagePicker();
            }
        });
        findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _editor.InsertLink();
            }
        });
        findViewById(R.id.action_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _editor.InsertMap();
            }
        });
         findViewById(R.id.action_erase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _editor.clearAllContents();
            }
        });
    }
        
    
        

If you are using **Image Pickers** or **Map Marker Pickers**, Add the following into your **Activity**:

    
     @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == _editor.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK&& data != null && data.getData() != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                _editor.InsertImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
            _editor.RestoreState();
        }
        else if(requestCode== _editor.MAP_MARKER_REQUEST){
            _editor.InsertMap(data.getStringExtra("cords"));
        }
    }


###Adding Callbacks

     _editor.setEditorListener(new BaseClass.EditorListener() {
                @Override
                public void onTextChanged(EditText editText, Editable text) {
                   // Toast.makeText(EditorTestActivity.this, text,Toast.LENGTH_SHORT).show();
                }
            });

##API

 - `Render();` Render the editor. This method must be called to render the editor.
 
 - `Render(String html)` Render the editor with HTML as parameter.
 
 - `Render(EditorState state)` Render the editor with the state as parameter
 
 - `UpdateTextStyle(EditorTextStyle style);` Update the text style for
   the currently active block. Possible values are `H1,H2,H3,BOLD,ITALIC,INDENT and OUTDENT`  
   

 - `setH1TextSize(int size), setH2TextSize(int size) and setH3TextSize(int size)` Override the existing text sizes. There are getter methods as well to retrieve the existing text sizes for each.
 
 - `OpenImagePicker();` Opens up the image picker. Once the user has selected the image, it's automatically inserted to the editor. But you must configure a remote URL ,where you want the image to be uploaded. If the Remote URL is not specifed, the image is not persisted.

 - `InsertImage(Bitmap bitmap);` Insert a bitmap image into the editor.

 - `setImageUploaderUri(String Url);`used to configure the remote URL ,where you want the image to be uploaded. This is compulsory if you are using the Image Picker.
 
 - `setEditorImageLayout(int layout);` Override the default layout for images in the editor.

 - `InsertList(boolean isOrdered);`Insert an Ordered or Unordered List.
  
 - `setListItemLayout(int layout);` Override the default layout for list items.

 - `InsertDivider();` Insert a line divider
 
 - `setDividerLayout(int layout);` Override the default layout for dividers

 - `InsertMap():` Fires up the google map location picker activity. Once the user has selected the location, the library will automatically insert the marker with the location into editor.

 - `InsertMap(String Cords);` Insert the marker into the editor. The cordinates must be of string form,  `"LAT,LNG"`
 - `setMapViewLayout(int layout)` Override the default layout for maps in the editor
 
 - `RenderEditor(String html);` Used to render the content into the editor from an HTML string
 
 - `getContent();`  returns the content in the editor as `EditorState`
 
 - `getContentAsSerialized();` returns the content as serialized form of EditorState
 
 - `getContentAsSerialized(EditorState state);` returns the provided parameter as serialized.
 - `getContentAsHTML();` returns the editor content in HTML format. 


##Future Improvements


 - Insert quotes.
 - Underline and Overline selections.
 - Imrove and add more callbacks.

Contributions are much appreciated, feel free to fork and customize for your needs.
