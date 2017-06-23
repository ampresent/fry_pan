$().ready(function() {
    $("#login_form").validate({
        rules: {
            username: "required",
            password: {
                required: true,
                minlength: 4
            },
        },
        messages: {
            username: "Please fill in username.",
            password: {
                required: "Please fill in password.",
                minlength: jQuery.validator.format("Password can't be less than {0} characters.")
            },
        }
    });
    $("#register_form").validate({
        rules: {
            username: "required",
            password: {
                required: true,
                minlength: 5
            },
            rpassword: {
                equalTo: "#register_password"
            },
            email: {
                required: true,
                email: true
            }
        },
        messages: {
            username: "Please fill in your name.",
            password: {
                required: "Please fill in password.",
                minlength: jQuery.validator.format("Password can't be less than {0} characters.")
            },
            rpassword: {
                equalTo: "Passwords don't match."
            },
            email: {
                required: "Fill in email.",
                email: "Please fill in valid email."
            }
        }
    });
});
$(function() {
    $("#register_btn").click(function() {
        $("#register_form").css("display", "block");
        $("#login_form").css("display", "none");
    });
    $("#back_btn").click(function() {
        $("#register_form").css("display", "none");
        $("#login_form").css("display", "block");
    });
});