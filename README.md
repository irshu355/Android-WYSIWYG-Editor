Tetra-Native-Editor
===================




About
-------------


*Tetra-Native-Editor** is a WYSIWYG editor completely written in Android using the native components in the controls tree.




Demo
-------------

![enter image description here](https://scontent-kul1-1.xx.fbcdn.net/v/t1.0-9/13227159_269389593411500_6219375228375154304_n.jpg?oh=f6b5fe0ab000d0eb89915d0147ff24ef&oe=579B3038)  ![enter image description here](https://scontent-kul1-1.xx.fbcdn.net/v/t1.0-9/13177651_269390020078124_2120913121972781573_n.jpg?oh=4a94f50c352330007063dc7dd092da0d&oe=57E6CE25)  


Features
-------------

The editor is built, so that every part of the design have been exposed and is available for customization. You can define, how the editor should look like, and what are the controls, that should be available (the controls toolbar layout can also be created by yourself, just call the API methods on the click event).

**Available Controls:**

| Control     | Usage |
| :------- | ----: |
| `H1`,  `H2` and `H3` | Insert Headings | 
| `Bold`, `Italic`, `Underline` & `Overline`    | Format the text   | 
| `Image Picker`| Insert Images to the editor from storage or a URL    | 
| `Hyperlinks` | Add Links to the editor
|`Location Selector` | Use the embedded map editor to tag and insert locations to the editor |
|`Numbered` and `Bulleted` Lists | Let's you created Unorderd and Ordered lists |
|`Line Divider` | Add a divider among paragraphs or Headings
|`Clear Content` | Remove all contents from the editor



Usage
-------------------

**content_editor_test.xml:**

  

    <com.irshu.libs.Editor
        android:layout_width="match_parent"
        android:id="@+id/editor"
        app:render_type="Editor"
        app:placeholder="Start writing here..."
        android:paddingTop="10dp"
        android:paddingLeft="10dp"
        android:paddingRight="10dp"
        android:layout_height="wrap_content"
        android:paddingBottom="100dp"
    >

**EditorTestActivity.java:**


	 

 

     protected void onCreate(Bundle savedInstanceState) {

        _editor= (Editor) findViewById(R.id.editor);
        
        findViewById(R.id.action_header_1).setOnClickListener(new View.OnClickListener() {
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
        _editor.StartEditor();
    }
        
    
        

If you are using **Image Pickers** or **Map Marker Pickers**, Add the following too in **LayoutActivity.java**:

    
     @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == _editor.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK&& data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                _editor.InsertImage(bitmap);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
            Toast.makeText(getApplicationContext(), "It was canccelled", Toast.LENGTH_SHORT).show();
            _editor.RestoreState();
        }
        else if(requestCode== _editor.MAP_MARKER_REQUEST){
            _editor.InsertMap(data.getStringExtra("cords"), true);
        }
    }

> **Note:**

> - You should create the **Editor Toolbar by yourself**, or you could use the  **Embedded Toolbar** inside the library.

###Adding Callbacks

     _editor.setEditorListener(new BaseClass.EditorListener() {
                @Override
                public void onTextChanged(EditText editText, Editable text) {
                   // Toast.makeText(EditorTestActivity.this, text,        Toast.LENGTH_SHORT).show();
                }
            });

##Future Improvements


 - Insert quotes into editor
 - HTML Parser
 - Indent and Outdent selections
 - Add more Callbacks for the controls

Thank you for your support,
I will keep the library updated, contributions are much appreciated, feel free to fork and customize for your needs.
