![enter image description here](https://img.shields.io/badge/release-0.3.0-blue.svg)&nbsp; ![enter image description here](https://img.shields.io/badge/license-Apache 2-blue.svg)&nbsp; ![enter image description here](https://img.shields.io/badge/issues-0-green.svg)

Laser-Native-Editor
===================

**Laser-Native-Editor** is an iFrame free WYSIWYG editor that uses the native components in the content tree.

Download
------------
gradle:

    compile 'com.github.irshulx:laser-native-editor:0.3.0'

or maven:

    <dependency>
      <groupId>com.github.irshulx</groupId>
      <artifactId>laser-native-editor</artifactId>
      <version>0.3.0</version>
      <type>pom</type>
    </dependency>

Demo
--------------

![enter image description here](https://raw.githubusercontent.com/irshuLx/laser-native-editor/master/screens/gif-1.gif)&nbsp;&nbsp;&nbsp;&nbsp;![enter image description here](https://raw.githubusercontent.com/irshuLx/laser-native-editor/master/screens/gif-2.gif)&nbsp;&nbsp;&nbsp;&nbsp;![enter image description here](https://raw.githubusercontent.com/irshuLx/laser-native-editor/master/screens/gif-3.gif)&nbsp;&nbsp;&nbsp;![enter image description here](https://raw.githubusercontent.com/irshuLx/laser-native-editor/master/screens/editor_screen_1.jpg)&nbsp;&nbsp;&nbsp; ![enter image description here](https://raw.githubusercontent.com/irshuLx/laser-native-editor/master/screens/editor_screen_2.jpg)&nbsp;&nbsp;&nbsp; ![enter image description here](https://raw.githubusercontent.com/irshuLx/laser-native-editor/master/screens/editor_screen_3.jpg)  


Features
-------------

 - **Renderer or Editor**: You can use Laser as a **Renderer** to Render the content or use it as an **Editor** to create the content.
 
 - **No Webviews used** to render the content. Laser uses Native EditText, ImageView and as such to render the contents.
 
 - **HTML Parser:** Render your HTML Code into the editor and vice versa.
 
 - **Image Uploader Api:** Use the Built-in API to upload the images to the server.
 
 - **Integration with web based WYSIWYG's:** HTMLParser helps the Editor to work seemlessly with the WYSIWYG editor on your web platform.

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
 
 - `Render(String html);` Render the editor with HTML as parameter.
 
 - `Render(EditorState state);` Render the editor with the state as parameter
 
 - `getContent();`  returns the content in the editor as `EditorState`
 
 - `getContentAsSerialized();` returns the content as serialized form of EditorState
 
 - `getContentAsSerialized(EditorState state);` returns the provided parameter as serialized.
 
 - `getContentAsHTML();` returns the editor content in HTML format. 
 
 - `UpdateTextStyle(EditorTextStyle style);` Update the text style for
   the currently active block. Possible values are `H1,H2,H3,BOLD,ITALIC,INDENT and OUTDENT` .  

 - `setH1TextSize(int size), setH2TextSize(int size) and setH3TextSize(int size);` Override the existing text sizes. There are getter methods as well to retrieve the existing text sizes for each.
 
 - `setFontFace(int resource);` Sets the FontFace for the editor.
 
 - `setLineSpacing(float value);` Sets the linespace for the editor.
 
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
 - `setMapViewLayout(int layout);` Override the default layout for maps in the editor

## Available Fonts##

You are free to set any of the below fonts as the default font for your editor.  To apply the font, use the API, for e.g: `editor.setFontFace(R.string.fontFamily__cursive);`

![enter image description here](https://raw.githubusercontent.com/irshuLx/laser-native-editor/master/screens/font-style-1.jpg)&nbsp;&nbsp;&nbsp; ![enter image description here](https://raw.githubusercontent.com/irshuLx/laser-native-editor/master/screens/font-style-2.jpg)&nbsp;&nbsp;&nbsp; ![enter image description here](https://raw.githubusercontent.com/irshuLx/laser-native-editor/master/screens/font-style-3.jpg)  

| Font Name     | Resource | 
| :------- | :-----: |
| serif | `R.string.fontFamily__serif` | 
| sans-serif | `R.string.fontFamily__sans_serif` | 
| sans-serif-light | `R.string.fontFamily__sans_serif_light` | 
| sans-serif-condensed | `R.string.fontFamily__sans_serif_condensed` | 
| sans-serif-thin | `R.string.fontFamily__sans_serif_thin` | 
| Serifsans-serif-medium | `R.string.fontFamily__sans_serif_medium` | 
| serif-monospace | `R.string.fontFamily__serif_monospace` | 
| casual | `R.string.fontFamily__casual` | 
| cursive | `R.string.fontFamily__cursive` | 
| monospace | `R.string.fontFamily__monospace` | 

## Overridable layouts ##

You can create your own layouts with the same Id's with the required Id's and put them in your app's layout directory. App will then override the library's layout and pick the one from your app's layout directory. As of now, you can override the following layouts.

| Layout     | Description | Required Id's |
| :------- | :-----: |:-----: |
| [R.layout.editor_image_view](https://github.com/irshuLx/laser-native-editor/blob/master/laser-native-editor/src/main/res/layout/editor_image_view.xml) | Used to insert an image to the editor  | `@+id/progress`, `@+id/lblStatus`,`@+id/imageView`,`@+id/btn_remove`
| [R.layout.tmpl_list_item](https://github.com/irshuLx/laser-native-editor/blob/master/laser-native-editor/src/main/res/layout/tmpl_unordered_list_item.xml) | Used to insert an ordered/unordered list  | `@+id/lblOrder`, `@+id/txtText`,`@+id/lblText`


##Future Improvements


 - Insert quotes.
 - Underline and Overline selections.
 - Imrove and add more callbacks.

Contributions are much appreciated, feel free to fork and customize for your needs.

If you come across any bugs or needs, please mention it on issues, i will address it and resolve it the latest possible.

##Lisense

    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
    http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


