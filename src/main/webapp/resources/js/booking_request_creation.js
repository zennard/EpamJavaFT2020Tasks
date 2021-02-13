const form = document.requestForm;
const submitBtn = document.getElementById("submitBtn");
const types = document.getElementsByName("type");
const errorPanel = document.getElementById("error-panel");
const noDemandsErrorEl = document.getElementsByClassName("no-demands-error").item(0);
const fieldsEmptyErrorEl = document.getElementsByClassName("fields-empty-error").item(0);
const unnecessaryFieldsErrorEl = document.getElementsByClassName("unnecessary-fields-error").item(0);
const duplicateFieldsErrorEl = document.getElementsByClassName("duplicate-fields-error").item(0);
let usedTypes = [];

document.getElementById("submitBtn").addEventListener("click", onSubmit);

function addInputRow(bedsCountLabel, typeLabel) {
    const inputForm = createInputForm(bedsCountLabel, typeLabel);
    form.insertBefore(inputForm, submitBtn);
}

function removeLastInputRow() {
    const prev = $(submitBtn).prev().get();
    console.log($(prev).attr("class"));
    if ($(prev).hasClass("row")) {
        $(prev).remove();
    }
}

function createInputForm(bedsCountLabel, typeLabel) {
    const div = createDiv();
    const bedsCountFormGroup = createFormGroupDiv();
    const typeFormGroup = createFormGroupDiv();
    const bedsCountLabelFormGroup = createFormGroupDiv();
    const typeLabelFormGroup = createFormGroupDiv();
    const bedsCountInput = createBedsCountInput("1", "5");
    const typeSelect = createTypeSelect();
    const labelForInput = createLabelForInput(bedsCountInput, bedsCountLabel);
    const labelForSelect = createLabelForInput(typeSelect, typeLabel);

    types.forEach(type => {
        console.log("type:");
        console.log(type);
        const option = createOption(type.value, type.dataset.type);
        typeSelect.appendChild(option);
    })

    bedsCountLabelFormGroup.appendChild(labelForInput);
    bedsCountFormGroup.appendChild(bedsCountInput);
    typeLabelFormGroup.appendChild(labelForSelect);
    typeFormGroup.appendChild(typeSelect);

    div.appendChild(bedsCountLabelFormGroup)
    div.appendChild(bedsCountFormGroup);
    div.appendChild(typeLabelFormGroup)
    div.appendChild(typeFormGroup);

    console.log("div:");
    console.log(div);

    return div;
}

function onSubmit() {
    const bedsCountInputElems = document.getElementsByName("bedsCountInput");
    const selectTypeElems = document.getElementsByName("typeSelect");

    console.log(bedsCountInputElems);
    bedsCountInputElems.forEach(elem => console.log(elem.value.length));
    console.log(selectTypeElems);

    if (validate(bedsCountInputElems, selectTypeElems)) {
        form.submit();
    }
}

//@todo
function validate(bedsCountInputElems, selectTypeElems) {
    clearErrorPanel();
    if (!bedsCountInputElems || !selectTypeElems
        || bedsCountInputElems.length === 0 || selectTypeElems.length === 0) {
        if (!noDemandsErrorEl.classList.contains("visible")) {
            noDemandsErrorEl.classList.remove("invisible");
        }
        return false;
    }
    if (Array.from(bedsCountInputElems).filter(elem => !elem.value || elem.value === 0).length > 0
        || bedsCountInputElems.length !== selectTypeElems.length) {
        if (!fieldsEmptyErrorEl.classList.contains("visible")) {
            fieldsEmptyErrorEl.classList.remove("invisible");
        }
        return false;
    }
    if (selectTypeElems.length > types.length) {
        if (!unnecessaryFieldsErrorEl.classList.contains("visible")) {
            unnecessaryFieldsErrorEl.classList.remove("invisible");
        }
        return false;
    }

    const selectedTypes = Array.from(selectTypeElems).map(select => select.value);
    if (selectedTypes.length !== new Set(selectedTypes).size) {
        if (!duplicateFieldsErrorEl.classList.contains("visible")) {
            duplicateFieldsErrorEl.classList.remove("invisible");
        }
        return false;
    }
    return form.checkValidity();

}

function createLabelForInput(input, labelValue) {
    const label = document.createElement("label");
    label.textContent = labelValue;
    label.setAttribute("for", input.getAttribute("name"));
    return label;
}

function clearErrorPanel() {
    console.log(noDemandsErrorEl);
    console.log(fieldsEmptyErrorEl);
    console.log(unnecessaryFieldsErrorEl);
    console.log(duplicateFieldsErrorEl);

    noDemandsErrorEl.classList.add("invisible");
    fieldsEmptyErrorEl.classList.add("invisible");
    unnecessaryFieldsErrorEl.classList.add("invisible");
    duplicateFieldsErrorEl.classList.add("invisible");
}

function createFormGroupDiv() {
    const formGroup = document.createElement("div");
    formGroup.className = "form-group col-sm-2";
    return formGroup;
}

function createDiv() {
    const div = document.createElement("div");
    div.className = "row input-row justify-content-center"
    return div;
}

function createBedsCountInput(min, max) {
    const bedsCountInput = document.createElement("input");
    bedsCountInput.type = "number";
    bedsCountInput.name = "bedsCountInput";
    bedsCountInput.min = min;
    bedsCountInput.max = max;

    return bedsCountInput;
}

function createOption(type, value) {
    const typeOption = document.createElement("option");
    typeOption.label = type;
    typeOption.value = value;
    return typeOption;
}

function createTypeSelect() {
    const typeSelect = document.createElement("select");
    typeSelect.name = "typeSelect";
    return typeSelect;
}

