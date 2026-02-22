function authHeaders() {
  return { "Content-Type": "application/json" };
}

function countryCodeToKorean(code) {
  const map = { KR: "대한민국", JP: "일본", CN: "중국", US: "미국" };
  return map[code] || code || "-";
}

function genderToKorean(gender) {
  const map = { FEMALE: "여성", MALE: "남성", OTHER: "기타" };
  return map[gender] || "-";
}

function formatBirthDate(iso) {
  if (!iso) return "-";
  return iso;
}

function setRoleUI(role) {
  const currentRoleEl = document.getElementById("currentRole");
  if (currentRoleEl) currentRoleEl.value = role;

  document.querySelectorAll(".role-pill").forEach((btn) => {
    btn.classList.toggle("is-active", btn.dataset.role === role);
  });

  const hostPanel = document.getElementById("hostPanel");
  const userPanel = document.getElementById("userPanel");
  const hostCardPanel = document.getElementById("hostCardPanel");

  if (hostPanel) hostPanel.style.display = role === "HOST" ? "block" : "none";
  if (userPanel) userPanel.style.display = role === "USER" ? "block" : "none";
  if (hostCardPanel) hostCardPanel.style.display = role === "HOST" ? "block" : "none";

  const roleBadge = document.getElementById("roleBadge");
  if (roleBadge) roleBadge.textContent = roleToLabel(role);
}

function setHint(msg = "") {
  const hint = document.getElementById("saveHint");
  if (hint) hint.textContent = msg;
}

function lockBasicFields() {
  ["name", "birthDate", "gender", "country", "region"].forEach((id) => {
    const el = document.getElementById(id);
    if (!el) return;

    el.readOnly = true;
    el.setAttribute("readonly", "readonly");
    el.classList.add("readonly-input");
  });
}

async function loadHostCardPublicStatus() {
  const res = await authFetch("/api/mypage/host/card", { method: "GET" });
  if (!res || !res.ok) return;

  const data = await res.json();
  console.log("[host/card GET]", data);
  const toggle = document.getElementById("hostCardPublicToggle");
  if (toggle) toggle.checked = !!data.isPublic;
}

async function updateHostCardPublic(isPublic) {
  const res = await authFetch("/api/mypage/host/card", {
    method: "PATCH",
    headers: authHeaders(),
    body: JSON.stringify({ isPublic }),
  });

  if (!res || !res.ok) {
    alert("카드프로필 공개 설정 변경에 실패했습니다.");
    await loadHostCardPublicStatus();
  }
}

async function loadProfile() {
  const res = await authFetch("/api/mypage/profile", { method: "GET" });
  if (!res) return;

  if (!res.ok) {
    alert("프로필 정보를 불러오지 못했습니다.");
    return;
  }

  const data = await res.json();

  const displayNameEl = document.getElementById("displayName");
  const displayMetaEl = document.getElementById("displayMeta");
  const avatarEl = document.getElementById("avatar");

  if (displayNameEl) displayNameEl.textContent = data.name ?? "-";

  const meta = `${data.region ?? "-"} · ${data.age ?? "-"}세 · ${countryCodeToKorean(data.countryCode)}`;
  if (displayMetaEl) displayMetaEl.textContent = meta;

  if (avatarEl) {
    const url = (data.profileImageUrl ?? "").trim();

    if (url) {
      avatarEl.textContent = "";
      avatarEl.style.backgroundImage = `url('${url}')`;
      avatarEl.style.backgroundSize = "cover";
      avatarEl.style.backgroundPosition = "center";
      avatarEl.style.backgroundRepeat = "no-repeat";
    } else {
      avatarEl.style.backgroundImage = "";
      avatarEl.textContent = data.name ? data.name[0] : "?";
    }
  }


  const nameEl = document.getElementById("name");
  const birthEl = document.getElementById("birthDate");
  const genderEl = document.getElementById("gender");
  const countryEl = document.getElementById("country");
  const regionEl = document.getElementById("region");

  if (nameEl) nameEl.value = data.name ?? "";
  if (birthEl) birthEl.value = formatBirthDate(data.birthDate);
  if (genderEl) genderEl.value = genderToKorean(data.gender);
  if (countryEl) countryEl.value = countryCodeToKorean(data.countryCode);
  if (regionEl) regionEl.value = data.region ?? "";

  const mbtiEl = document.getElementById("mbti");
  const smokingEl = document.getElementById("smoking");
  const drinkingEl = document.getElementById("drinking");
  const bioEl = document.getElementById("bio");

  if (mbtiEl) mbtiEl.value = data.mbti ?? "";

  if (smokingEl) {
    smokingEl.value = data.smoking === true ? "true" : data.smoking === false ? "false" : "";
  }

  if (drinkingEl) {
    drinkingEl.value = data.drinking === true ? "true" : data.drinking === false ? "false" : "";
  }

  if (bioEl) bioEl.value = data.bio ?? "";

  setRoleUI(data.role ?? "USER");

  lockBasicFields();

  if ((data.role ?? "USER") === "HOST") {
    await loadHostCardPublicStatus();
  }
}

// =============================
function bindProfileSave() {
  const form = document.getElementById("profileForm");
  if (!form) return;

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    setHint("");

    const mbtiRaw = document.getElementById("mbti")?.value ?? "";
    const mbtiTrim = mbtiRaw.trim();
    const mbti = mbtiTrim === "" ? null : mbtiTrim;

    if (mbti && !/^[EI][SN][TF][JP]$/i.test(mbti)) {
          setHint("MBTI를 제대로 입력해 주세요.");
          document.getElementById("mbti")?.focus();
          return;
        }

    const smokingRaw = document.getElementById("smoking")?.value ?? "";
    const smoking = smokingRaw === "" ? null : smokingRaw === "true";

    const drinkingRaw = document.getElementById("drinking")?.value ?? "";
    const drinking = drinkingRaw === "" ? null : drinkingRaw === "true";

    const bio = document.getElementById("bio")?.value?.trim() || null;

    const payload = {
        mbti: mbti ? mbti.toUpperCase() : null,
        smoking,
        drinking,
        bio
        };

    const res = await authFetch("/api/mypage/profile", {
      method: "PUT",
      headers: authHeaders(),
      body: JSON.stringify(payload),
    });
    if (!res) return;

    if (res.ok) {
      setHint("저장되었습니다.");
     return;
    } else {
          const text = await res.text().catch(() => "");
          setHint(text || "저장에 실패했습니다. 입력값을 확인해 주세요.");
        }
    });
}

async function setRole(role) {
  setRoleUI(role);

  const res = await authFetch("/api/mypage/role", {
    method: "PATCH",
    headers: authHeaders(),
    body: JSON.stringify({ role }),
  });
  if (!res) return;

  if (!res.ok) {
    const text = await res.text().catch(() => "");
    alert(text || "역할 변경에 실패했습니다. 잠시 후 다시 시도해 주세요.");
    await loadProfile();
    return;
  }

  setHint("역할이 변경되었습니다.");
  await loadProfile();
}

window.setRole = setRole;

function bindAccountActions() {
  document.getElementById("logoutBtn")?.addEventListener("click", async () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("userId");
    localStorage.removeItem("email");
    localStorage.removeItem("role");
    sessionStorage.removeItem("justLoggedIn");

    try {
      await fetch("/api/auth/logout", {
        method: "POST",
        credentials: "same-origin",
      });
    } catch (e) {
      console.warn("[logout] request failed:", e);
    }

    window.location.href = "/";
  });

  document.getElementById("deactivateBtn")?.addEventListener("click", async () => {
    const ok = confirm("정말로 회원 탈퇴하시겠습니까?\n탈퇴 후에는 로그인이 불가능합니다.");
    if (!ok) return;

    const res = await authFetch("/api/auth/withdraw", { method: "DELETE" });
    if (!res) return;

    if (!res.ok) {
      const text = await res.text().catch(() => "");
      alert(text || "회원 탈퇴에 실패했습니다. 잠시 후 다시 시도해 주세요.");
      return;
    }

    try {
      await fetch("/api/auth/logout", {
        method: "POST",
        credentials: "same-origin",
      });
    } catch (e) {
      console.warn("[logout after deactivate] request failed:", e);
    }

    alert("회원 탈퇴가 완료되었습니다.");
    window.location.href = "/";
  });

  document.getElementById("resetPwBtn")?.addEventListener("click", () => {
    window.location.href = "/auth/forgot-password";
  });
}

document.addEventListener("DOMContentLoaded", async () => {
  bindMyPageLanguageSelect();
  bindProfileSave();
  bindAccountActions();

  setRoleUI(document.getElementById("currentRole")?.value || "USER");

  const toggle = document.getElementById("hostCardPublicToggle");
  if (toggle) {
    toggle.addEventListener("change", () => {
      updateHostCardPublic(toggle.checked);
    });
  }

  await loadProfile();
});

function roleToLabel(role) {
  if (role === "HOST") return "Guide";
  if (role === "USER") return "Traveler";
  return "-";
}

const MY_LANG_KEY = "mypageLang";

const I18N = {
  ko: {
    "basic.title": "기본 정보",
    "basic.nickname": "닉네임",
    "basic.nicknameHint": "닉네임은 회원가입 후 변경할 수 없습니다.",
    "basic.birth": "생년월일",
    "basic.birthHint": "생년월일은 회원가입 후 변경할 수 없습니다.",
    "basic.gender": "성별",
    "basic.genderHint": "성별은 회원가입 후 변경할 수 없습니다.",
    "basic.country": "국가",
    "basic.countryHint": "국가는 회원가입 후 변경할 수 없습니다.",
    "basic.region": "지역",
    "basic.regionHint": "지역은 회원가입 후 변경할 수 없습니다.",
    "basic.role": "회원 유형",
    "basic.roleHint": "Guide로 전환 시 투어 등록 및 관리 기능이 활성화됩니다.",

    "role.traveler": "여행자",
    "role.guide": "가이드",

    "edit.mbti": "MBTI",
    "edit.smoking": "흡연",
    "edit.drinking": "음주",
    "edit.bio": "자기소개",

    "actions.save": "저장",

    "hostCard.title": "가이드 카드 프로필",
    "hostCard.public": "카드 프로필 공개",
    "hostCard.hint":
      "카드프로필을 공개해야 게시글을 작성할 수 있으며, 다른 사용자에게 최소 정보(이름, 나이, 성별, MBTI, 자기소개)가 표시됩니다.",

    "account.title": "계정 설정",
    "account.deactivate": "회원 탈퇴",
    "account.deactivateHint": "탈퇴 후에는 더 이상 로그인할 수 없습니다.",

    "side.details": "상세 정보",
    "side.detailsDesc": "계정 정보 및 설정을 확인하고 수정할 수 있습니다.",
  },

  ja: {
    "basic.title": "基本情報",
    "basic.nickname": "ニックネーム",
    "basic.nicknameHint": "ニックネームは登録後に変更できません。",
    "basic.birth": "生年月日",
    "basic.birthHint": "生年月日は登録後に変更できません。",
    "basic.gender": "性別",
    "basic.genderHint": "性別は登録後に変更できません。",
    "basic.country": "国",
    "basic.countryHint": "国は登録後に変更できません。",
    "basic.region": "地域",
    "basic.regionHint": "地域は登録後に変更できません。",
    "basic.role": "会員タイプ",
    "basic.roleHint": "ガイドに切り替えると、ツアーの登録・管理機能が有効になります。",

    "role.traveler": "旅行者",
    "role.guide": "ガイド",

    "edit.mbti": "MBTI",
    "edit.smoking": "喫煙",
    "edit.drinking": "飲酒",
    "edit.bio": "自己紹介",

    "actions.save": "保存",

    "hostCard.title": "ガイドカードプロフィール",
    "hostCard.public": "カードプロフィールを公開",
    "hostCard.hint":
      "カードプロフィールを公開すると投稿が可能になり、他のユーザーに最小情報（名前、年齢、性別、MBTI、自己紹介）が表示されます。",

    "account.title": "アカウント設定",
    "account.deactivate": "退会",
    "account.deactivateHint": "退会後はログインできません。",

    "side.details": "詳細情報",
    "side.detailsDesc": "アカウント情報と設定を確認・編集できます。",
  },

  en: {
    "basic.title": "Basic Info",
    "basic.nickname": "Nickname",
    "basic.nicknameHint": "Your nickname cannot be changed after sign-up.",
    "basic.birth": "Birth Date",
    "basic.birthHint": "Your birth date cannot be changed after sign-up.",
    "basic.gender": "Gender",
    "basic.genderHint": "Your gender cannot be changed after sign-up.",
    "basic.country": "Country",
    "basic.countryHint": "Your country cannot be changed after sign-up.",
    "basic.region": "Region",
    "basic.regionHint": "Your region cannot be changed after sign-up.",
    "basic.role": "Member Type",
    "basic.roleHint": "Switching to Guide enables tour creation and management.",

    "role.traveler": "Traveler",
    "role.guide": "Guide",

    "edit.mbti": "MBTI",
    "edit.smoking": "Smoking",
    "edit.drinking": "Drinking",
    "edit.bio": "Bio",

    "actions.save": "Save",

    "hostCard.title": "Guide Card Profile",
    "hostCard.public": "Public Profile",
    "hostCard.hint":
      "You must set your card profile to public to create posts. Minimal info (name, age, gender, MBTI, bio) will be shown to others.",

    "account.title": "Account Settings",
    "account.deactivate": "Deactivate Account",
    "account.deactivateHint": "After deactivation, you will no longer be able to log in.",

    "side.details": "Details",
    "side.detailsDesc": "View and edit your account details and settings.",
  },
};

function applyMyPageLanguage(lang) {
  const dict = I18N[lang] || I18N.ko;

  document.querySelectorAll("[data-i18n]").forEach((el) => {
    const key = el.dataset.i18n;
    const text = dict[key];
    if (text) el.textContent = text;
  });

  const smoke = document.getElementById("smoking");
  const drink = document.getElementById("drinking");
  if (smoke) {
    smoke.options[0].text = lang === "ja" ? "選択" : lang === "en" ? "Select" : "선택";
    smoke.options[1].text = lang === "ja" ? "はい" : lang === "en" ? "Yes" : "예";
    smoke.options[2].text = lang === "ja" ? "いいえ" : lang === "en" ? "No" : "아니오";
  }
  if (drink) {
    drink.options[0].text = lang === "ja" ? "選択" : lang === "en" ? "Select" : "선택";
    drink.options[1].text = lang === "ja" ? "はい" : lang === "en" ? "Yes" : "예";
    drink.options[2].text = lang === "ja" ? "いいえ" : lang === "en" ? "No" : "아니오";
  }

  setHint("");
}

function bindMyPageLanguageSelect() {
  const select = document.getElementById("langSelect");
  if (!select) return;

  const saved = localStorage.getItem(MY_LANG_KEY) || "ko";
  select.value = saved;
  applyMyPageLanguage(saved);

  select.addEventListener("change", () => {
    const lang = select.value;
    localStorage.setItem(MY_LANG_KEY, lang);
    applyMyPageLanguage(lang);

    loadProfile();
  });
}

