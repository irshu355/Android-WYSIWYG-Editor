

[ ![Download](https://api.bintray.com/packages/irshu/maven/laser-native-editor/images/download.svg) ](https://bintray.com/irshu/maven/laser-native-editor/_latestVersion)&nbsp;![enter image description here](https://img.shields.io/badge/issues-16-yellow.svg)&nbsp;[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Android-WYSIWYG-Editor
===================


An iframe free text editor that uses native components in the content tree. Motivation was to create a clean native feel WYSIWYG editor like medium.com has.

[<img src="https://github.com/irshuLx/Android-WYSIWYG-Editor/raw/master/screens/google-play-badge.png">](https://play.google.com/store/apps/details?id=com.github.irshulx.wysiwyg_editor)


## Changelog

## [3.0.4 - 30 July 2019]

 - Updates glide to 4.9.0


## [3.0.3 - 20 December 2018]

 - **Introducing Macro's** - Macro's are equivalent to components in react/vue.js. 
It lets you add a custom block into the editor where you get to control what gets rendered into the editor. Read more about this below on Macro's section.

- Add blockquote support

- Replaced image loader library **Picasso** with **Glide**, so to make use of it's rich customization api.
 
- An improved editor navigation.


## [2.3.2 - 01 December 2018]

 - Links on editor will now respond to click
 - Fix for editor rendering wrong order from serialised string/html

Please find all the latest releases/changelogs on https://github.com/irshuLx/Android-WYSIWYG-Editor/releases

 
 Contributions
------------

You can clone from the `master` branch. Once ready to merge , please open a pull request to `dev` branch. Be sure to merge the latest from "upstream" before making a pull request! I can then review and merge it back to master

Download
------------
gradle:

    compile 'com.github.irshulx:laser-native-editor:3.0.3'

or maven:

    <dependency>
      <groupId>com.github.irshulx</groupId>
      <artifactId>laser-native-editor</artifactId>
      <version>3.0.3</version>
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

The editor is built, so that every part of the design have been exposed and is available for customization.

**Available Controls:**

| Control     | Usage |
| :------- | :-----: |
| `H1`,  `H2` and `H3` | Insert Headings |
| `Blockquote` | Insert a blockquote |
| `Bold`, `Italic`,`Color`, `Intent` & `Outdent`    | Format the text   |
| `Image Picker`| Insert Images to the editor from storage or a URL    |
| `Hyperlinks` | Add Links to the editor
|`Location Selector` | Use the embedded map editor to tag and insert locations to the editor |
|`Numbered` and `Bulleted` Lists | Let's you created Unorderd and Ordered lists |
|`Line Divider` | Add a divider



Usage
-------------------

For a complete overview of the implementation, please take a look at [EditorTestActivity.java](https://github.com/irshuLx/Android-WYSIWYG-Editor/blob/master/sample/src/main/java/com/github/irshulx/wysiwyg/EditorTestActivity.java)

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
        
           findViewById(R.id.action_color).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextColor("#FF3333");
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
	

        findViewById(R.id.action_erase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.clearAllContents();
            }
        });
	
	findViewById(R.id.action_blockquote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.BLOCKQUOTE);
            }
        });
        
        editor.render();
        
    }



If you are using **Image Pickers**,  Add the following into your **Activity**:


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
    }

You can also programmatically append text into the editor using HTML like so:

    editor.render("<p>Hello man, whats up!</p>");
    editor.render("<div>This is another paragraph!</div>");
Please be reminded, nested HTML **ARE NOT** supported at the moment except for `<ul>` and `<ol>`, for eg: `<p><h2>Hello world</h2></p>` **won't work** since `<h2>` is nested inside `<p>`. Following HTML tags are supported:

 - `<p>`,`<div>`
 - `<h1>`,`<h2>`,`<h3>`
 - `<blockquote>`
 - `<img>`
 - `<ul>`,`<ol>`
 - `<hr/>`
 - `<br/>`
 
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
   the currently active block. Possible values are `H1,H2,H3,BOLD,ITALIC,BLOCKQUOTE,INDENT and OUTDENT` .

 - `setH1TextSize(int size), setH2TextSize(int size) and setH3TextSize(int size);` Override the existing text sizes. There are getter methods as well to retrieve the existing text sizes for each.

 - `setFontFace(int resource);` Sets the FontFace for the editor.

 - `setLineSpacing(float value);` Sets the linespace for the editor.
 
 - `setEditorTextColor("#FF3333");` Sets the global text color of the editor (default is #000000).
 
 - `updateTextColor("#FF3333");` Changes the text color of the focused text.

 - `openImagePicker();` Opens up the image picker. Once the user has selected the image, it's automatically inserted to the editor. But you must configure a remote URL ,where you want the image to be uploaded. If the Remote URL is not specifed, the image is not persisted.

 - `insertImage(Bitmap bitmap);` Insert a bitmap image into the editor.

 - `setEditorImageLayout(int layout);` Override the default layout for images in the editor.

 - `insertList(boolean isOrdered);`Insert an Ordered or Unordered List.

 - `setListItemLayout(int layout);` Override the default layout for list items.
 
 - `insertMacro(String name,View view, Map<String, Object> props);` Insert macro(custom component).

 - `insertDivider();` Insert a line divider

 - `setDividerLayout(int layout);` Override the default layout for dividers

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

## Macro's ##

A macro is equivalent to a component in react/vue.js. 

for eg: 
In vue, you define a component `author-tag` as:


     Vue.component('author-tag', {  
         props: ['author-name', 'date'],
         template: '<div>
                      <span class="name" {{author-name}}></span>
                      <span class="date" {{date}}></span>
                   </div>'  
        });

You can then use this component as a custom HTML element in your markup:

    <author-tag author-name="Thomas Isaac" date="12 July 2018">
    </author-tag>

When a **custom html tag** is found on the HTML text that's fed to the editor,  `View onRenderMacro(String name, Map<String, Object> props, int index);` will be invoked. All you need to do is to inflate your custom view created to handle `author-tag` and return that view to the editor: 

    editor.setEditorListener(new EditorListener() {
	    ...
	    @Override  
	    public View onRenderMacro(String name, Map<String, Object> props, int index) {  
	        View layout = getLayoutInflater().inflate(R.layout.layout_authored_by, null);  
	        TextView lblName = layout.findViewById(R.id.lbl_author_name);
	        lblName.setText(props.get("author-name"));
		return layout;  
	     }
    });
To insert a macro, use:

    private void insertAuthorMacro() {  
      View layout = getLayoutInflater().inflate(R.layout.layout_authored_by, null);  
      Map<String, Object> props = new HashMap<>();  
      props.put("author-name", "Thomas Isaac");  
      props.put("date","12 July 2018");  
      editor.insertMacro("author-tag",layout, props);  
    }

When content is extracted using `editor.getContentAsHTML();` the html string will contain:

    "<author-tag author-name=\"Thomas Isaac\" date=\"12 July 2018\"></author-tag>"

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
| [R.layout.tmpl_image_view](https://github.com/irshuLx/laser-native-editor/blob/master/laser-native-editor/src/main/res/layout/tmpl_image_view.xml) | To insert an image to the editor  | `@+id/progress`, `@+id/lblStatus`,`@+id/imageView`,`@+id/btn_remove`
| [R.layout.tmpl_list_item](https://github.com/irshuLx/laser-native-editor/blob/master/laser-native-editor/src/main/res/layout/tmpl_list_item.xml) | To insert an ordered/unordered list  | `@+id/lblOrder`, `@+id/txtText`,`@+id/lblText`
| [R.layout.tmpl_divider_layout](https://github.com/irshuLx/laser-native-editor/blob/master/laser-native-editor/src/main/res/layout/tmpl_divider_layout.xml) | To insert a line divider  | -

You could also set the layouts via the API:

 -  `editor.setEditorImageLayout(R.layout.tmpl_image_view);`

 -  `editor.setListItemLayout(R.layout.tmpl_list_item);`

 -  `editor.setDividerLayout(R.layout.tmpl_divider_layout);`

If you have **minifyEnabled**, below are the proguard rules:

      -keep class com.google.gson.** { *; }  
      -dontwarn com.squareup.picasso.**  
      -dontwarn com.squareup.okhttp.**  
      -keep public class org.jsoup.** { public *; }


Best Practices
-------------------

Since the endusers are **hard typing the content**, it's always considered **good idea** to **backup the content every specific interval**  to be safe.

    timer = new Timer();  
    timer.scheduleAtFixedRate(new TimerTask() {  
      @Override  
      public void run() {  
         String text = editor.getContentAsSerialized();
         sharedPreferences.putString(String.format("backup-{0}",  new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(new Date())),   text);
         sharedPreferences.apply();
      }
    }, 0, backupInterval);

Future Plans
-------------------


 - Add videos support
 
 - Address the issues and feature requests from fellow devs.

Contributions are much appreciated, feel free to fork and customize for your needs.

If you come across any bugs or needs, please mention it on issues, i will address it and resolve it the latest possible.

Libraries Used
-------------------

 - Glide, Gson, Jsoup
 
 
 

## License

    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
    http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

