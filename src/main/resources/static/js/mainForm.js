/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


$(function () {
    bs_input_file_vitor()

    $(".vtr-input-box .vtr-label").on("click", function () {
        // alert( $(this).val());
        $(this).parent().find(".vtr-input").trigger("focus");
    });
    $(".vtr-input-box .vtr-input").on("focus", function () {
        // alert( $(this).val());
        $(this).parent().addClass("vtr-active");
    });
    $(".vtr-input-box .vtr-input").on("focusout", function () {
        if (!$(this).val())
            $(this).parent().removeClass("vtr-active");
    });
});

function bs_input_file_vitor() {
    jQuery(".input-file").before(function () {
        if (!jQuery(this).prev().hasClass("input-ghost")) {
            var i = jQuery("#cf7-file");
            return 	i.addClass("input-ghost"),
                    i.change(function () {
                        i.next(i).find("input").val(i.val().split("\\").pop())
                    }),
                    jQuery(this).find("button.btn-choose").click(function () {
                i.click();
            }),
                    jQuery(this).find("button.btn-reset").click(function () {
                i.val(null), jQuery(this).parents(".input-file").find("input").val("")
            }),
                    jQuery(this).find("input").css("cursor", "pointer"),
                    jQuery(this).find("input").mousedown(function () {
                return jQuery(this).parents(".input-file").prev().click(), !1
            }), i
        }
    })
}
