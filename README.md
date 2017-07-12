[ ![Download](https://api.bintray.com/packages/irshu/maven/laser-native-editor/images/download.svg) ](https://bintray.com/irshu/maven/laser-native-editor/_latestVersion)&nbsp;![enter image description here](https://img.shields.io/badge/issues-8-red.svg)

Android-WYSIWYG-Editor
===================


An iframe free text editor that uses native components in the content tree.

[<img src="https://github.com/irshuLx/Android-WYSIWYG-Editor/raw/master/screens/play-icon.jpg">](https://play.google.com/store/apps/details?id=com.github.irshulx.wysiwyg)


What's New in 2.0.0
------------

 - **A better editor**
 - **Automatic image upload functionality has been removed**. You will have to do the upload on your own, just pass the uri to the editor.
 - **Custom fonts**. You can now apply custom fonts for the editor ( with option for seperate fonts for header and content)
 - **Subtitle option for images**

Download
------------
gradle:

    compile 'com.github.irshulx:laser-native-editor:2.0.2'

or maven:

    <dependency>
      <groupId>com.github.irshulx</groupId>
      <artifactId>laser-native-editor</artifactId>
      <version>2.0.2</version>
      <type>pom</type>
    </dependency>

Demo
--------------

![](https://github.com/irshuLx/Android-WYSIWYG-Editor/raw/master/screens/ezgif-3-4b5b0fc2bd.gif)&nbsp;![](https://github.com/irshuLx/Android-WYSIWYG-Editor/raw/master/screens/ezgif-3-3c5a0f84f2.gif)&nbsp;&nbsp;![](https://github.com/irshuLx/Android-WYSIWYG-Editor/raw/master/screens/ezgif-3-b3c73d7be8.gif)&nbsp;&nbsp;





![](https://github.com/irshuLx/Android-WYSIWYG-Editor/raw/master/screens/screenshot1.png)&nbsp; ![](https://github.com/irshuLx/Android-WYSIWYG-Editor/raw/master/screens/screenshot2.png)&nbsp; ![](https://github.com/irshuLx/Android-WYSIWYG-Editor/raw/master/screens/screenshot3.png)![](https://github.com/irshuLx/Android-WYSIWYG-Editor/raw/master/screens/screenshot4.png)
![](https://github.com/irshuLx/Android-WYSIWYG-Editor/raw/master/screens/screenshot5.png)


Features
-------------

 - **Renderer or Editor**: You can use it as a **Renderer** to Render the content or use it as an **Editor** to create the content.

 - **No Webviews used** to render the content. It uses Native EditText, ImageView and as such to render the contents.

 - **HTML Parser:** Render your HTML Code into the editor and vice versa.

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

For a complete overview of the implementation, please take a look at [EditorTestActivity.java](https://github.com/irshuLx/laser-native-editor/blob/master/app/src/main/java/com/example/mkallingal/qapp/EditorTestActivity.java)

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

        editor = (Editor) findViewById(R.id.editor);
        findViewById(R.id.action_h1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.H1);
            }
        });

        findViewById(R.id.action_h2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.H2);
            }
        });

        findViewById(R.id.action_h3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.H3);
            }
        });

        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.BOLD);
            }
        });

        findViewById(R.id.action_Italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.ITALIC);
            }
        });

        findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.INDENT);
            }
        });

        findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.OUTDENT);
            }
        });

        findViewById(R.id.action_bulleted).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.insertList(false);
            }
        });

        findViewById(R.id.action_unordered_numbered).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.insertList(true);
            }
        });

        findViewById(R.id.action_hr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.insertDivider();
            }
        });

        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.openImagePicker();
            }
        });

        findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.insertLink();
            }
        });

        findViewById(R.id.action_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.insertMap();
            }
        });

        findViewById(R.id.action_erase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.clearAllContents();
            }
        });
    }
    editor.render(); 



If you are using **Image Pickers** or **Map Marker Pickers**, Add the following into your **Activity**:


         @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == editor.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK&& data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                editor.insertImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
           // editor.RestoreState();
        }
        else if(requestCode== editor.MAP_MARKER_REQUEST){
            editor.insertMap(data.getStringExtra("cords"));
        }
    }



API
-------------------

 - `render();` Render the editor. This method must be called to render the editor.

 - `render(String html);` Render the editor with HTML as parameter.

 - `render(EditorState state);` Render the editor with the state as parameter

 - `getContent();`  returns the content in the editor as `EditorState`

 - `getContentAsSerialized();` returns the content as serialized form of EditorState

 - `getContentAsSerialized(EditorState state);` returns the provided parameter as serialized.

 - `getContentAsHTML();` returns the editor content in HTML format.

 - `updateTextStyle(EditorTextStyle style);` Update the text style for
   the currently active block. Possible values are `H1,H2,H3,BOLD,ITALIC,INDENT and OUTDENT` .

 - `setH1TextSize(int size), setH2TextSize(int size) and setH3TextSize(int size);` Override the existing text sizes. There are getter methods as well to retrieve the existing text sizes for each.

 - `setFontFace(int resource);` Sets the FontFace for the editor.

 - `setLineSpacing(float value);` Sets the linespace for the editor.

 - `openImagePicker();` Opens up the image picker. Once the user has selected the image, it's automatically inserted to the editor. But you must configure a remote URL ,where you want the image to be uploaded. If the Remote URL is not specifed, the image is not persisted.

 - `insertImage(Bitmap bitmap);` Insert a bitmap image into the editor.

 - `setEditorImageLayout(int layout);` Override the default layout for images in the editor.

 - `insertList(boolean isOrdered);`Insert an Ordered or Unordered List.

 - `setListItemLayout(int layout);` Override the default layout for list items.

 - `insertDivider();` Insert a line divider

 - `setDividerLayout(int layout);` Override the default layout for dividers

 - `insertMap():` Fires up the google map location picker activity. Once the user has selected the location, the library will automatically insert the marker with the location into editor.

 - `insertMap(String Cords);` Insert the marker into the editor. The cordinates must be of string form,  `"LAT,LNG"`
 - `setMapViewLayout(int layout);` Override the default layout for maps in the editor

If you are using image uploads, use the below to add the uploaded image to editor:

      editor.setEditorListener(new EditorListener() {
                @Override
                public void onTextChanged(EditText editText, Editable text) {
                    // Toast.makeText(EditorTestActivity.this, text, Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onUpload(Bitmap image, String uuid) {
                   
                   //do your upload image operations here, once done, call onImageUploadComplete and pass the url and uuid as reference.
                    editor.onImageUploadComplete("http://www.videogamesblogger.com/wp-content/uploads/2015/08/metal-gear-solid-5-the-phantom-pain-cheats-640x325.jpg",uuid);
                   // editor.onImageUploadFailed(uuid);
                }
            });

## Custom Fonts ##

You can set your own fonts for the editor.

        Map<Integer, String> headingTypeface = getHeadingTypeface();
        Map<Integer, String> contentTypeface = getContentface();
        editor.setHeadingTypeface(headingTypeface);
        editor.setContentTypeface(contentTypeface);

         public Map<Integer,String> getHeadingTypeface() {
        Map<Integer, String> typefaceMap = new HashMap<>();
        typefaceMap.put(Typeface.NORMAL,"fonts/GreycliffCF-Bold.ttf");
        typefaceMap.put(Typeface.BOLD,"fonts/GreycliffCF-Heavy.ttf");
        typefaceMap.put(Typeface.ITALIC,"fonts/GreycliffCF-Heavy.ttf");
        typefaceMap.put(Typeface.BOLD_ITALIC,"fonts/GreycliffCF-Bold.ttf");
        return typefaceMap;
    }

    public Map<Integer,String> getContentface() {
        Map<Integer, String> typefaceMap = new HashMap<>();
        typefaceMap.put(Typeface.NORMAL,"fonts/Lato-Medium.ttf");
        typefaceMap.put(Typeface.BOLD,"fonts/Lato-Bold.ttf");
        typefaceMap.put(Typeface.ITALIC,"fonts/Lato-MediumItalic.ttf");
        typefaceMap.put(Typeface.BOLD_ITALIC,"fonts/Lato-BoldItalic.ttf");
        return typefaceMap;
    }
        


## Overridable layouts ##

You can create your own layouts with the same Id's with the required Id's and put them in your app's layout directory. App will then override the library's layout and pick the one from your app's layout directory. As of now, you can override the following layouts.

| Layout     | Description | Required Id's |
| :------- | :-----: |:-----: |
| [R.layout.tmpl_image_view](https://github.com/irshuLx/laser-native-editor/blob/master/laser-native-editor/src/main/res/layout/tmpl_image_view.xml) | Used to insert an image to the editor  | `@+id/progress`, `@+id/lblStatus`,`@+id/imageView`,`@+id/btn_remove`
| [R.layout.tmpl_list_item](https://github.com/irshuLx/laser-native-editor/blob/master/laser-native-editor/src/main/res/layout/tmpl_list_item.xml) | Used to insert an ordered/unordered list  | `@+id/lblOrder`, `@+id/txtText`,`@+id/lblText`
| [R.layout.tmpl_divider_layout](https://github.com/irshuLx/laser-native-editor/blob/master/laser-native-editor/src/main/res/layout/tmpl_divider_layout.xml) | Used to insert a line divider  | -

You could also set the layouts via the API:

 -  `editor.setEditorImageLayout(R.layout.tmpl_image_view);`

 -  `editor.setListItemLayout(R.layout.tmpl_list_item);`

 -  `editor.setDividerLayout(R.layout.tmpl_divider_layout);`


Future Plans
-------------------


 - Insert quotes.
 
 - Improve and add more callbacks.
 
 - Address the issues and feature requests from fellow devs.

Contributions are much appreciated, feel free to fork and customize for your needs.

If you come across any bugs or needs, please mention it on issues, i will address it and resolve it the latest possible.

##License

    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
    http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
