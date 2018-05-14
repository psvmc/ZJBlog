/**
 * @desc: 公用js 
 * @depend: jQuery, jQuery scrollTo
 */
$(document).ready(function() {
	/**
	 * scrollTo Top
	 */
	var scrollTo = {
		nodeName : "J-backTop",
		scrollHeight : "100",
		linkBottom : "110px",
		linkRight : "18px",
		_scrollTop : function() {
			if (jQuery.scrollTo) {
				jQuery.scrollTo(0, 800, {
					queue : true
				});
			}
		},
		_scrollScreen : function() {
			var that = this, topLink = $('#' + that.nodeName);

			if (jQuery(document).scrollTop() <= that.scrollHeight) {
				topLink.hide();
				return true;
			} else {
				topLink.fadeIn();
			}
		},
		_resizeWindow : function() {
			var that = this, topLink = $('#' + that.nodeName);
			if ($(window).width() > 1024) {
				var leftSize = $(window).width() * 0.95;
				topLink.css({
					'right' : '18px',
					'bottom' : that.linkBottom,
					'z-index' : 9999
				});
			} else {
				topLink.css({
					'left' : '',
					'right' : that.linkRight,
					'bottom' : that.linkBottom,
					'z-index' : 9999
				});
			}

		},
		run : function() {
			var that = this, topLink = $('<a id="' + that.nodeName + '" href="#" class="toTop"><i class="fa fa-chevron-up"></i></a>');
			topLink.appendTo($('body'));

			topLink.css({
				'display' : 'none',
				'position' : 'fixed',
				'left' : '',
				'right' : that.linkRight,
				'bottom' : that.linkBottom
			});

			that._resizeWindow();

			if (jQuery.scrollTo) {
				topLink.click(function() {
					that._scrollTop();
					return false;
				});
			}
			jQuery(window).resize(function() {
				that._scrollScreen();
				that._resizeWindow();
			});
			jQuery(window).scroll(function() {
				that._scrollScreen();
			});
		}
	}
	scrollTo.run();
});

function zjChangeImageSize(){
	var headDivWidth = $("#ztheme-head-img-div").width();
    var headDivHeight = $("#ztheme-head-img-div").height();
    if(headDivWidth/headDivHeight <= 1920.0/1080 ){
		$("#ztheme-head-img").css("height","100%");
    }else{
		$("#ztheme-head-img").css("height",null);
    }
}

$(document).ready(function() {
    var headImgArr = [
    	"/jekyll-themes/ztheme/images/hdbg01.jpg",
    	"/jekyll-themes/ztheme/images/hdbg02.jpg",
    	"/jekyll-themes/ztheme/images/hdbg03.jpg"
    ];

    var item = Math.floor(Math.random() * (headImgArr.length));

    $("#ztheme-head-img").attr("src",headImgArr[item]);
	$("#ztheme-head-img-div").css("display","block")
    zjChangeImageSize();

    $(window).resize(function() { 
		zjChangeImageSize();
	});
});