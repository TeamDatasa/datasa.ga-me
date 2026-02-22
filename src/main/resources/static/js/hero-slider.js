(() => {
  const slider = document.getElementById("heroSlider");
  const track = document.getElementById("heroTrack");
  const slides = Array.from(track.querySelectorAll(".yw-heroSlide"));
  const prevBtn = document.getElementById("heroPrev");
  const nextBtn = document.getElementById("heroNext");
  const dotsWrap = document.getElementById("heroDots");

  if (!slider || !track || slides.length === 0) return;

  let index = 0;
  let timer = null;
  const AUTOPLAY_MS = 4500;

  slides.forEach((_, i) => {
    const b = document.createElement("button");
    b.type = "button";
    b.className = "yw-heroDot" + (i === 0 ? " is-active" : "");
    b.setAttribute("aria-label", `${i + 1}번 슬라이드`);
    b.addEventListener("click", () => go(i, true));
    dotsWrap.appendChild(b);
  });

  const dots = Array.from(dotsWrap.querySelectorAll(".yw-heroDot"));

  function render() {
    track.style.transform = `translateX(-${index * 100}%)`;
    slides.forEach((s, i) => s.classList.toggle("is-active", i === index));
    dots.forEach((d, i) => d.classList.toggle("is-active", i === index));
  }

  function go(i, userAction = false) {
    index = (i + slides.length) % slides.length;
    render();
    if (userAction) restart();
  }

  function next() { go(index + 1, true); }
  function prev() { go(index - 1, true); }

  function start() {
    stop();
    timer = setInterval(() => go(index + 1, false), AUTOPLAY_MS);
  }
  function stop() {
    if (timer) clearInterval(timer);
    timer = null;
  }
  function restart() {
    start();
  }

  nextBtn?.addEventListener("click", next);
  prevBtn?.addEventListener("click", prev);

  slider.addEventListener("mouseenter", stop);
  slider.addEventListener("mouseleave", start);

  render();
  start();
})();