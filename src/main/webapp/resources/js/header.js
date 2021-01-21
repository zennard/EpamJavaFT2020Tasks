function addLangAttribute(lang) {
    const url = new URL(window.location.href);
    console.log(url.toString())
    url.searchParams.set('lang', lang);
    window.location = url;
    console.log(url.toString());
    console.log(lang);
    console.log(window.location.href);
    console.log(window.location);
}