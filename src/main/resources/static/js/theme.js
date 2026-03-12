document.addEventListener("DOMContentLoaded", () => {
  const THEME_PROFILE_IMG = {
    FOOD: "/images/theme/food.png",
    CITY: "/images/theme/city.png",
    NATURE: "/images/theme/nature.png",
    HEALING: "/images/theme/healing.png",
    SHOPPING: "/images/theme/shopping.png",
    HISTORY: "/images/theme/history.png",
    ACTIVITY: "/images/theme/activity.png",
    NIGHT: "/images/theme/night.png",
    PHOTO: "/images/theme/photo.png",
    TRADITION: "/images/theme/tradition.png",
    DEFAULT: "/images/theme/default.png",
  };

  function getThemeProfileImg(theme) {
    if (!theme) return THEME_PROFILE_IMG.DEFAULT;
    const key = String(theme).trim().toUpperCase();
    return THEME_PROFILE_IMG[key] || THEME_PROFILE_IMG.DEFAULT;
  }


  document.querySelectorAll(".yw-card[data-theme]").forEach(card => {
    const theme = card.dataset.theme;
    const img = card.querySelector(".ai-theme-avatar");
    if (img) img.src = getThemeProfileImg(theme);
  });
});