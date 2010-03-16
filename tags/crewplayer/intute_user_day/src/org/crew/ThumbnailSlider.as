import flash.geom.Rectangle;

import org.crew.CrewPlayer;
import org.crew.Utils;

/**
 * A slider that shows thumbnails and allows seeking based on times
 */
class org.crew.ThumbnailSlider {

    private var viewPort:MovieClip = null;

    private var upButton:MovieClip = null;

    private var downButton:MovieClip = null;

    private var slider:MovieClip = null;

    private var bar:MovieClip = null;

    private var clipSheet:MovieClip = null;

    private var thumbs:Array = null;

    private var thumbUrls:Array = null;

    private var thumbsLoaded:Array = null;

    private var thumbTimes:Array = null;

    private var thumbEndTimes:Array = null;

    private var selectedThumb:Number = -1;

    private var x:Number = 0;

    private var y:Number = 0;

    private var width:Number = 0;

    private var height:Number = 0;

    private var depth:Number = 0;

    private var backgroundColour:Number;

    private var borderColour:Number;

    private var buttonColour:Number;

    private var selectColour:Number;

    private var parent:CrewPlayer = null;

    private var isSliding:Boolean = false;

    private var slideStartY:Number = 0;

    private var scrollTimerId:Number = 0;

    private var startScrollTime:Number = 0;

    private var intervalId:Number = -1;

    public function ThumbnailSlider(parent:CrewPlayer, x:Number, y:Number,
            width:Number, height:Number, depth:Number, thumbUrls:Array,
            thumbTimes:Array, thumbEndTimes:Array,  backgroundColour:Number,
            borderColour:Number, buttonColour:Number, selectColour:Number) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.backgroundColour = backgroundColour;
        this.borderColour = borderColour;
        this.buttonColour = buttonColour;
        this.selectColour = selectColour;
        this.thumbs = new Array();
        this.thumbUrls = thumbUrls;
        this.thumbsLoaded = new Array();
        this.thumbTimes = thumbTimes;
        this.thumbEndTimes = thumbEndTimes;
        this.viewPort = parent.createEmptyMovieClip("viewPort", depth);
        this.bar = viewPort.createEmptyMovieClip("bar", depth + 10);
        this.slider = viewPort.createEmptyMovieClip("slider", depth + 20);
        this.clipSheet = viewPort.createEmptyMovieClip("clipSheet", depth + 1);
        this.upButton = viewPort.createEmptyMovieClip("upButton", depth + 25);
        this.downButton = viewPort.createEmptyMovieClip("downButton",
            depth + 26);

        var loader:MovieClipLoader = new MovieClipLoader();
        loader.addListener(this);
        for (var i:Number = 0; i < thumbUrls.length; i++) {
            thumbs.push(clipSheet.createEmptyMovieClip("thumb" + i, i));
            thumbsLoaded.push(false);
            loader.loadClip(thumbUrls[i], thumbs[i]);
        }

        this.onSliderPress.thumbnailSlider = this;
        this.onSliderRelease.thumbnailSlider = this;
        this.onSliderMove.thumbnailSlider = this;
        this.onUpButtonPress.thumbnailSlider = this;
        this.onUpButtonRelease.thumbnailSlider = this;
        this.onDownButtonPress.thumbnailSlider = this;
        this.onDownButtonRelease.thumbnailSlider = this;
        this.onClipSheetPress.thumbnailSlider = this;
        this.onBarPress.thumbnailSlider = this;
        this.onBarRelease.thumbnailSlider = this;
        slider.onPress = this.onSliderPress;
        slider.onRelease = this.onSliderRelease;
        slider.onReleaseOutside = this.onSliderRelease;
        slider.onMouseMove = this.onSliderMove;
        upButton.onPress = this.onUpButtonPress;
        upButton.onRelease = this.onUpButtonRelease;
        upButton.onReleaseOutside = this.onUpButtonRelease;
        downButton.onPress = this.onDownButtonPress;
        downButton.onRelease = this.onDownButtonRelease;
        downButton.onReleaseOutside = this.onDownButtonRelease;
        clipSheet.onPress = this.onClipSheetPress;
        bar.onPress = this.onBarPress;
        bar.onRelease = this.onBarRelease;
        bar.onReleaseOutside = this.onBarRelease;
        Mouse.addListener(this);
    }

    public function onLoadError(clip:MovieClip):Void {
        var allDone:Boolean = true;
        for (var i:Number = 0; i < thumbs.length; i++) {
            if (thumbs[i] == clip) {
                parent.logger.debug("Error loading " + thumbUrls[i]);
                thumbsLoaded[i] = true;
            }
            if (!thumbsLoaded[i]) {
                parent.logger.debug("Not loaded " + thumbUrls[i]);
                allDone = false;
            }
        }
        if (allDone) {
            layoutThumbs();
        }
    }

    public function onLoadInit(clip:MovieClip):Void {
        var allDone:Boolean = true;
        for (var i:Number = 0; i < thumbs.length; i++) {
            if (thumbs[i] == clip) {
                parent.logger.debug("Loaded " + thumbUrls[i]);
                thumbsLoaded[i] = true;
            }
            if (!thumbsLoaded[i]) {
                parent.logger.debug("Not loaded " + thumbUrls[i]);
                allDone = false;
            }
        }
        if (allDone) {
            layoutThumbs();
        }
    }

    public function layoutThumbs() {
        parent.logger.debug("Laying out thumbnails");
        var nextY:Number = 10;
        for (var i:Number = 0; i < thumbs.length; i++) {
            thumbs[i]._y = nextY;
            thumbs[i]._x = 5;
            nextY += thumbs[i]._height + 10;
        }

        clipSheet._x = 0;
        clipSheet._y = 0;
        clipSheet.scrollRect = new Rectangle(0, 0, width, height);

        var scale = 100 * (height / clipSheet._height);
        if (scale <= 100) {
            slider._yscale = scale;
        }
    }

    public function draw():Void {
        viewPort._x = x;
        viewPort._y = y;
        viewPort.lineStyle(1, borderColour, 100, false, "normal", "round",
            "round", 3);
        viewPort.beginFill(backgroundColour);
        viewPort.moveTo(0, 0);
        viewPort.lineTo(width - 10, 0);
        viewPort.lineTo(width - 10, height);
        viewPort.lineTo(0, height);
        viewPort.endFill();

        bar._x = width - 30;
        bar._y = 1;
        bar.beginGradientFill("linear", [borderColour, backgroundColour],
            [100, 100], [0, 0xFF],
            {a:5, b:0, c:0, d:0, e:5, f:0, g:5, h:5, i:1});
        bar.moveTo(0, 0);
        bar.lineTo(16, 0);
        bar.lineTo(16, height - 2);
        bar.lineTo(0, height - 2);
        bar.endFill();

        var buttonTextColour = buttonColour
            - Utils.getLowlight(buttonColour, 0x444444);
        Utils.drawButton(upButton, buttonColour, width - 29, 1, 18, 17, true,
            true);
        upButton.lineStyle(3, buttonTextColour, 100, true, "none",
            "none", "miter", 3);
        upButton.moveTo(3, 12);
        upButton.lineTo(9, 5);
        upButton.lineTo(15, 12);
        Utils.drawButton(downButton, buttonColour, width - 29, height - 18,
            18, 17, true, true);
        downButton.lineStyle(3, buttonTextColour, 100, true, "none",
            "none", "miter", 3);
        downButton.moveTo(3, 5);
        downButton.lineTo(9, 12);
        downButton.lineTo(15, 5);
        Utils.drawButton(slider, buttonColour, width - 29, 19, 18, height - 41,
            true, true);

        viewPort._width = width;
        viewPort._height = height;
    }

    public function onSliderPress():Void {
        var thumbnailSlider:ThumbnailSlider = arguments.callee.thumbnailSlider;
        var slider:MovieClip = thumbnailSlider.slider;
        thumbnailSlider.slideStartY = slider._parent._ymouse - slider._y;
        thumbnailSlider.isSliding = true;
        Utils.drawButton(slider, thumbnailSlider.buttonColour,
            slider._x, slider._y, slider._width - 1, slider._height - 1, false,
            false);
    }

    public function onSliderRelease():Void {
        var thumbnailSlider:ThumbnailSlider = arguments.callee.thumbnailSlider;
        var slider:MovieClip = thumbnailSlider.slider;
        thumbnailSlider.isSliding = false;
        Utils.drawButton(slider, thumbnailSlider.buttonColour,
            slider._x, slider._y, slider._width - 1, slider._height - 1, true,
            false);
    }

    private function moveSlider(sliderY:Number) {
        var top = upButton._y + upButton._height;
        var bottom = downButton._y - slider._height;
        if (sliderY < top) {
            sliderY = top;
        } else if (sliderY > bottom) {
            sliderY = bottom;
        }
        slider._y = sliderY;
        var sheetY:Number =
            ((((100 * height) / slider._yscale) - height + 20)
                * (sliderY - top))
                    / (bottom - top);
        clipSheet.scrollRect = new Rectangle(0, sheetY, width, height);
    }

    public function onSliderMove():Void {
        var thumbnailSlider:ThumbnailSlider = arguments.callee.thumbnailSlider;
        var slider:MovieClip = thumbnailSlider.slider;
        if (thumbnailSlider.isSliding) {
            var sliderY = slider._parent._ymouse - thumbnailSlider.slideStartY;
            thumbnailSlider.moveSlider(sliderY);
        }
    }

    public function onUpButtonPress():Void {
        var thumbnailSlider:ThumbnailSlider = arguments.callee.thumbnailSlider;
        var upButton = thumbnailSlider.upButton;
        Utils.drawButton(upButton, thumbnailSlider.buttonColour,
            upButton._x, upButton._y, upButton._width - 1, upButton._height - 1,
            false, false);
        var date:Date = new Date();
        thumbnailSlider.moveSlider(thumbnailSlider.slider._y - 1);
        thumbnailSlider.startScrollTime = date.getTime();
        thumbnailSlider.scrollTimerId = setInterval(thumbnailSlider, "moveUp",
            50);
    }

    public function onUpButtonRelease():Void {
        var thumbnailSlider:ThumbnailSlider = arguments.callee.thumbnailSlider;
        var upButton = thumbnailSlider.upButton;
        clearInterval(thumbnailSlider.scrollTimerId);
        Utils.drawButton(upButton, thumbnailSlider.buttonColour,
            upButton._x, upButton._y, upButton._width - 1, upButton._height - 1,
            true, false);
    }

    public function onDownButtonPress():Void {
        var thumbnailSlider:ThumbnailSlider = arguments.callee.thumbnailSlider;
        var downButton = thumbnailSlider.downButton;
        Utils.drawButton(downButton, thumbnailSlider.buttonColour,
            downButton._x, downButton._y, downButton._width - 1,
            downButton._height - 1, false, false);
        var date:Date = new Date();
        thumbnailSlider.moveSlider(thumbnailSlider.slider._y + 1);
        thumbnailSlider.startScrollTime = date.getTime();
        thumbnailSlider.scrollTimerId = setInterval(thumbnailSlider, "moveDown",
            50);
    }

    public function onDownButtonRelease():Void {
        var thumbnailSlider:ThumbnailSlider = arguments.callee.thumbnailSlider;
        var downButton = thumbnailSlider.downButton;
        clearInterval(thumbnailSlider.scrollTimerId);
        Utils.drawButton(downButton, thumbnailSlider.buttonColour,
            downButton._x, downButton._y, downButton._width - 1,
            downButton._height - 1, true, false);
    }

    public function onBarPress():Void {
        var thumbnailSlider:ThumbnailSlider = arguments.callee.thumbnailSlider;
        var date:Date = new Date();
        var movement = thumbnailSlider.slider._height;
        var func = "jumpUp";
        if (thumbnailSlider.viewPort._ymouse >
                (thumbnailSlider.slider._y + thumbnailSlider.slider._height)) {
            movement = -movement;
            func = "jumpDown";
        }
        thumbnailSlider.moveSlider(thumbnailSlider.slider._y - movement);
        thumbnailSlider.startScrollTime = date.getTime();
        thumbnailSlider.scrollTimerId = setInterval(thumbnailSlider, func,
            50);
    }

    public function onBarRelease():Void {
        var thumbnailSlider:ThumbnailSlider = arguments.callee.thumbnailSlider;
        clearInterval(thumbnailSlider.scrollTimerId);
    }

    private function moveUp():Void {
        var date:Date = new Date();
        if ((date.getTime() - startScrollTime) > 500) {
            var raised = true;
            if (upButton.hitTest(_root._xmouse, _root._ymouse, false)) {
                moveSlider(slider._y - 1);
                raised = false;
            }
            Utils.drawButton(upButton, buttonColour, upButton._x, upButton._y,
                upButton._width - 1, upButton._height - 1, raised, false);
        }
    }

    private function moveDown():Void {
        var date:Date = new Date();
        if ((date.getTime() - startScrollTime) > 500) {
            var raised = true;
            if (downButton.hitTest(_root._xmouse, _root._ymouse, false)) {
                moveSlider(slider._y + 1);
                raised = false;
            }
            Utils.drawButton(downButton, buttonColour, downButton._x,
                downButton._y, downButton._width - 1, downButton._height - 1,
                raised, false);
        }
    }

    private function jumpUp():Void {
        var date:Date = new Date();
        if ((date.getTime() - startScrollTime) > 500) {
            if (bar.hitTest(_root._xmouse, _root._ymouse, false)
                    && (viewPort._ymouse < slider._y)) {
                moveSlider(slider._y - slider._height);
            }
        }
    }

    private function jumpDown():Void {
        var date:Date = new Date();
        if ((date.getTime() - startScrollTime) > 500) {
            if (bar.hitTest(_root._xmouse, _root._ymouse, false)
                    && (viewPort._ymouse > (slider._y + slider._height))) {
                moveSlider(slider._y + slider._height);
            }
        }
    }

    private function deselectThumb() {
        if (selectedThumb != -1) {
            clipSheet.beginFill(backgroundColour);
            clipSheet.moveTo(thumbs[selectedThumb]._x - 5,
                thumbs[selectedThumb]._y - 5);
            clipSheet.lineTo(thumbs[selectedThumb]._x
                    + thumbs[selectedThumb]._width + 5,
                thumbs[selectedThumb]._y - 5);
            clipSheet.lineTo(thumbs[selectedThumb]._x
                    + thumbs[selectedThumb]._width + 5,
                thumbs[selectedThumb]._y + thumbs[selectedThumb]._height + 5);
            clipSheet.lineTo(thumbs[selectedThumb]._x - 5,
                thumbs[selectedThumb]._y + thumbs[selectedThumb]._height + 5);
            clipSheet.endFill();
            selectedThumb = -1;
        }
    }

    private function selectThumb(i:Number) {
        deselectThumb();
        clipSheet.beginFill(selectColour);
        clipSheet.moveTo(thumbs[i]._x - 5, thumbs[i]._y - 5);
        clipSheet.lineTo(thumbs[i]._x + thumbs[i]._width + 5,
            thumbs[i]._y - 5);
        clipSheet.lineTo(thumbs[i]._x + thumbs[i]._width + 5,
            thumbs[i]._y + thumbs[i]._height + 5);
        clipSheet.lineTo(thumbs[i]._x - 5,
            thumbs[i]._y + thumbs[i]._height + 5);
        clipSheet.endFill();
        selectedThumb = i;
    }

    public function onClipSheetPress():Void {
        var thumbnailSlider:ThumbnailSlider = arguments.callee.thumbnailSlider;
        var clipSheet:MovieClip = thumbnailSlider.clipSheet;
        var thumbs:Array = thumbnailSlider.thumbs;
        var thumbTimes:Array = thumbnailSlider.thumbTimes;

        for (var i:Number = 0; i < thumbs.length; i++) {
            if ((clipSheet._ymouse >= thumbs[i]._y)
                 && (clipSheet._ymouse <= (thumbs[i]._y + thumbs[i]._height))) {
                thumbnailSlider.selectThumb(i);
                thumbnailSlider.parent.seek(thumbTimes[i]);
            }
        }
    }

    public function onMouseWheel(delta:Number, scrollTarget:MovieClip):Void {
        if (viewPort.hitTest(_root._xmouse, _root._ymouse, false)) {
            var sliderY = slider._y - (delta * 5);
            moveSlider(sliderY);
        }
    }

    public function start():Void {
        if (intervalId == -1) {
            intervalId = setInterval(this, "update", 500);
        }
    }

    public function stop():Void {
        if (intervalId != -1) {
            clearInterval(intervalId);
            intervalId = -1;
        }
    }

    public function update():Void {
        var time:Number = parent.getTime();
        if (selectedThumb != -1) {
            if ((time < thumbTimes[selectedThumb])
                    || (time > thumbEndTimes[selectedThumb])) {
                deselectThumb();
            }
        }
        if (selectedThumb != -1) {
            for (var i:Number = 0; i < thumbTimes.length; i++) {
                if ((time >= thumbTimes[i]) && (time <= thumbEndTimes[i])) {
                    selectThumb(i);
                }
            }
        }
    }
}