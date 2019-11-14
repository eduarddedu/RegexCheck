function createInputElement(value) {
    const input = document.createElement('input');
    input.setAttribute('style', 'display:none');
    input.setAttribute('name', 'id');
    input.setAttribute('value', value);
    return input;
}

function appendRegexIdParam(form, regexId) {
    const newInput = createInputElement(regexId);
    const oldInput = form.querySelector('input');
    if (oldInput !== null) {
        form.replaceChild(newInput, oldInput);
    } else {
        form.appendChild(newInput);
    }
    const submitBtn = form.querySelector('button');
    submitBtn.removeAttribute('disabled');
}
function handleRadioInputClick(event) {
    const forms = [document.querySelector('#edit'), document.querySelector('#delete')];
    forms.forEach(form => appendRegexIdParam(form, event.target.id));
}

const radioInputs = document.querySelectorAll('input[type=radio]');

for (let i = 0; i < radioInputs.length; i++) {
    const radioIn = radioInputs.item(i);
    radioIn.addEventListener('click', handleRadioInputClick);
}
