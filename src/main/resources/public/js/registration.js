angular.module("registration_form", [])
    .controller("RegistrationCtrl", function ($scope, $http) {
        $scope.auth = {};
        $scope.sendForm = function (auth) {
            $http({
                method: "POST",
                url: "/register",
                data: $.param(auth),
                headers: {"Content-Type": "application/x-www-form-urlencoded"}
            }).then(
                function (data) {
                    window.alert("User was registered!");
                },
                function (error) {
                    window.alert("Something went wrong during registration!");
                }
            );
        }
    });