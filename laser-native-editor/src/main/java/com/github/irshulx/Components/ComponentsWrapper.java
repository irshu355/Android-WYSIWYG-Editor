package com.github.irshulx.Components;

public class ComponentsWrapper {
    private InputExtensions inputExtensions;
    private DividerExtensions dividerExtensions;
    private HTMLExtensions htmlExtensions;
    private ImageExtensions imageExtensions;
    private ListItemExtensions listItemExtensions;
    private MapExtensions mapExtensions;
    private MacroExtensions macroExtensions;

    public ComponentsWrapper(InputExtensions inputExtensions, DividerExtensions dividerExtensions, HTMLExtensions htmlExtensions, ImageExtensions imageExtensions, ListItemExtensions listItemExtensions, MapExtensions mapExtensions, MacroExtensions macroExtensions) {
        this.inputExtensions = inputExtensions;
        this.dividerExtensions = dividerExtensions;
        this.htmlExtensions = htmlExtensions;
        this.imageExtensions = imageExtensions;
        this.listItemExtensions = listItemExtensions;
        this.mapExtensions = mapExtensions;
        this.macroExtensions = macroExtensions;
    }

    public InputExtensions getInputExtensions() {
        return inputExtensions;
    }

    public DividerExtensions getDividerExtensions() {
        return dividerExtensions;
    }

    public HTMLExtensions getHtmlExtensions() {
        return htmlExtensions;
    }

    public ImageExtensions getImageExtensions() {
        return imageExtensions;
    }

    public ListItemExtensions getListItemExtensions() {
        return listItemExtensions;
    }

    public MapExtensions getMapExtensions() {
        return mapExtensions;
    }

    public MacroExtensions getMacroExtensions() {
        return macroExtensions;
    }

    public static  class Builder{
        private InputExtensions _inputExtensions;
        private DividerExtensions _dividerExtensions;
        private HTMLExtensions _htmlExtensions;
        private ImageExtensions _imageExtensions;
        private ListItemExtensions _listItemExtensions;
        private MapExtensions _mapExtensions;
        private MacroExtensions _macroExtensions;

        public Builder inputExtensions(InputExtensions inputExtensions){
            this._inputExtensions = inputExtensions;
            return this;
        }

        public Builder htmlExtensions(HTMLExtensions htmlExtensions){
            this._htmlExtensions = htmlExtensions;
            return this;
        }

        public Builder listItemExtensions(ListItemExtensions listItemExtensions){
            this._listItemExtensions = listItemExtensions;
            return this;
        }

        public Builder mapExtensions(MapExtensions mapExtensions){
            this._mapExtensions = mapExtensions;
            return this;
        }

        public Builder imageExtensions(ImageExtensions imageExtensions){
            this._imageExtensions = imageExtensions;
            return this;
        }

        public Builder macroExtensions(MacroExtensions macroExtensions){
            this._macroExtensions = macroExtensions;
            return this;
        }

        public Builder dividerExtensions(DividerExtensions dividerExtensions){
            this._dividerExtensions = dividerExtensions;
            return this;
        }

        public ComponentsWrapper build(){
            return new ComponentsWrapper(
                    this._inputExtensions,
                    this._dividerExtensions,
                    this._htmlExtensions,
                    this._imageExtensions,
                    this._listItemExtensions,
                    this._mapExtensions,
                    this._macroExtensions);
        }

    }
}
