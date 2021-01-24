const check = function () {
    if (document.getElementById('inputPassword').value ==
        document.getElementById('confirmPassword').value) {
        document.getElementById('message').innerHTML = '';
        document.getElementById('submit').disabled = false;
    } else {
        document.getElementById('message').style.color = 'red';
        document.getElementById('message').innerHTML = 'Not matching';
        document.getElementById('submit').disabled = true;
    }
}