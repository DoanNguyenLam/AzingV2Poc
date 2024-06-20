document.addEventListener("DOMContentLoaded", (event) => {
  const emailCards = document.querySelectorAll(".list-mails .card.email-card");
  emailCards.forEach(async function (card) {
    card.addEventListener("click", async function () {
      await handleClick(card);
    });
  });

  const listLabels = document.querySelectorAll(".ai-tool .label-items .label-form")
  listLabels.forEach(async (label) => {
    label.addEventListener("click", async function() {
      await updateLabel(label)
    })
  })

  const spinnerElement = () => {
    const spinner = document.createElement("div");
    spinner.className = "spinner-wrapper d-flex justify-content-center";

    const spinnerBorder = document.createElement("div");
    spinnerBorder.className = "spinner-border";
    spinnerBorder.setAttribute("role", "status");
    spinner.appendChild(spinnerBorder);

    return spinner;
  };

  const loading = () => {
    const cardBodys = document.querySelectorAll(
        ".card .card-body .body.mt-3"
    );
    cardBodys.forEach((item) => {
      item.appendChild(spinnerElement())
    });
  };

  const removeElement = () => {
    const noSelecteds = document.querySelectorAll(".no-selected");

    const currentBody = document.querySelector(
        ".original-email .card .card-body .body .original"
    );

    const currentSummary = document.querySelector(
        ".email-summary .card .card-body .body .summary"
    );
    const currentReplySuggestion = document.querySelector(
        ".reply-suggestion .card .card-body .body .reply-suggestion"
    );

    const spinners = document.querySelectorAll(".spinner-wrapper");
    spinners.forEach((item) => {
      item.remove();
    });

    if (noSelecteds && noSelecteds.length > 0) {
      noSelecteds.forEach((item) => item.remove());
    }
    if (currentBody) currentBody.remove();
    if (currentSummary) currentSummary.remove();
    if (currentReplySuggestion) currentReplySuggestion.remove();
  };

  const fetchData = async (card) => {
    card.submit()
  }

  const updateLabel = async (label) => {
    label.submit()
  }

  const handleClick = async (card) => {
    // Active class
    emailCards.forEach(function (card) {
      card.classList.remove("active");
    });
    card.classList.add("active");

    // Remove old data
    removeElement();

    // Call api to get summary and suggestions
    loading();
    await fetchData(card)

  };
});
