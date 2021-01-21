function onApartmentClick() {
    console.log(event.target);
    console.log(event.target.getAttribute('action'));
    const url = new URL('http://localhost:8088' + event.target.getAttribute('action'));
    console.log(url.toString())
    console.log(checkInInput.value);
    url.searchParams.set('startsAt', checkInInput.value);
    url.searchParams.set('endsAt', checkOutInput.value);
    window.location = url;
}