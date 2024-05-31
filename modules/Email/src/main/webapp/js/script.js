document.addEventListener("DOMContentLoaded", (event) => {
  const emailCards = document.querySelectorAll(".card.email-card");

  emailCards.forEach(function (card) {
    card.addEventListener("click", function () {
      const subject = card.querySelector(".card-title").innerText;
      const data = card.querySelector(".card-text").innerText;
      const date = card.querySelector(".date p").innerText;

      const emailData = {
        subject,
        data,
        date,
      };
      handleClick(card, emailData);
    });
  });

  const removeElement = () => {
    const noSelecteds = document.querySelectorAll(".no-selected");

    const currentSubject = document.querySelector(
      ".original-email .card .card-body .body .subject"
    );
    const currentBody = document.querySelector(
      ".original-email .card .card-body .body .body"
    );

    const currentSummary = document.querySelector(
      ".email-summary .card .card-body .body .summary"
    );
    const currentReplySuggestion = document.querySelector(
      ".reply-suggestion .card .card-body .body .reply-suggestion"
    );

    if (noSelecteds && noSelecteds.length > 0) {
      noSelecteds.forEach((item) => item.remove());
    }
    if (currentSubject) currentSubject.remove();
    if (currentBody) currentBody.remove();
    if (currentSummary) currentSummary.remove();
    if (currentReplySuggestion) currentReplySuggestion.remove();
  };

  const handleClick = async (card, data) => {

    // Active class
    emailCards.forEach(function (card) {
      card.classList.remove("active");
    });
    card.classList.add("active");

    // Render data
    // Remove old data
    removeElement();

    //  Add new data
    const originalEmailHTML = document.querySelector(
      ".original-email .card .card-body .body"
    );

    const subject = document.createElement("div");
    subject.className = "subject";
    const h5 = document.createElement("h5");
    h5.innerText = data.subject;
    subject.appendChild(h5);

    const body = document.createElement("div");
    body.classList.add("body", "mt-3");
    body.innerHTML = data.data;

    originalEmailHTML.appendChild(subject);
    originalEmailHTML.appendChild(body);

    // Call service summary & suggestions
    const summaryEmailHTML = document.querySelector(
      ".email-summary .card .card-body .body"
    );
    const summary = document.createElement("p");
    summary.className = "summary";
    summary.innerText = `
        - Lorem ipsum dolor sit amet consectetur adipisicing elit.
        - Architecto molestias dolor incidunt nisi tempora, provident nesciunt culpa maxime, quis, quibusdam a temporibus similique tenetur recusandae facere!
        - Impedit eius sequi atque?
    `;
    summaryEmailHTML.appendChild(summary);

    const suggestionEmailHTML = document.querySelector(
      ".reply-suggestion .card .card-body .body"
    );
    const replySuggestion = document.createElement("div");
    replySuggestion.className = "reply-suggestion";
    replySuggestion.innerHTML = `
    <div class="">
      <div class="aHl"></div>
      <div id=":1z" tabindex="-1"></div>
      <div id=":29" class="ii gt"
          jslog="20277; u014N:xr6bB; 1:WyIjdGhyZWFkLWY6MTc5OTkxNDE0NTg0NDQzMTk1NyJd; 4:WyIjbXNnLWY6MTc5OTkxNDE0NTg0NDQzMTk1NyJd">
          <div id=":2a" class="a3s aiL ">Hey cabien1307!<br>
              <br>
              You’ve just enabled two-factor authentication.<br>
              <br>
              Please take a moment to check that you have saved your recovery codes in
              a
              safe place. You can<br>
              download your recovery codes at:<br>
              <br>
              <a href="https://github.com/settings/auth/recovery-codes"
                  rel="noreferrer" target="_blank"
                  data-saferedirecturl="https://www.google.com/url?q=https://github.com/settings/auth/recovery-codes&amp;source=gmail&amp;ust=1716964458244000&amp;usg=AOvVaw1L77yFaG1Ezkv-7Gq_dGD6">https://github.com/settings/au<wbr>th/recovery-codes</a><br>
              <br>
              Recovery codes are the only way to access your account again. By saving
              your<br>
              recovery codes, you’ll be able to regain access if you:<br>
              <br>
              * Lose your phone<br>
              * Delete your authenticator app<br>
              * Change your phone number<br>
              <br>
              GitHub Support will not be able to restore access to your account.<br>
              <br>
              To disable two-factor authentication, visit<br>
              <a href="https://github.com/settings/security" rel="noreferrer"
                  target="_blank"
                  data-saferedirecturl="https://www.google.com/url?q=https://github.com/settings/security&amp;source=gmail&amp;ust=1716964458245000&amp;usg=AOvVaw0LvM1dI5nwxRd9t4pQsgy7">https://github.com/settings/se<wbr>curity</a><br>
              <br>
              More information about two-factor authentication can be found on GitHub
              Help
              at<br>
              <a href="https://docs.github.com/articles/about-two-factor-authentication"
                  rel="noreferrer" target="_blank"
                  data-saferedirecturl="https://www.google.com/url?q=https://docs.github.com/articles/about-two-factor-authentication&amp;source=gmail&amp;ust=1716964458245000&amp;usg=AOvVaw3Y1bOdeLmsU2QluRUaolbX">https://docs.github.com/articl<wbr>es/about-two-factor-authentica<wbr>tion</a><br>
              <br>
              If you have any questions, please visit <a
                  href="https://support.github.com" rel="noreferrer" target="_blank"
                  data-saferedirecturl="https://www.google.com/url?q=https://support.github.com&amp;source=gmail&amp;ust=1716964458245000&amp;usg=AOvVaw1swFwNVnLxH9cwZUbNX1IT">https://support.github.com</a>.<br>
              <br>
              Thanks,<br>
              Your friends at GitHub<div class="yj6qo"></div>
              <div class="adL"><br>
              </div>
          </div>
      </div>
      <div class="WhmR8e" data-hash="0"></div>
  </div>
    `;
    suggestionEmailHTML.appendChild(replySuggestion);
  };
});