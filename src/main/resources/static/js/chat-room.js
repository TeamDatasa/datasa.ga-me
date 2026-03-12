/* ===== 전역 변수 (HTML에서 주입됨) ===== */
// window.ROOM_ID
// window.USER_ID

const roomId = window.ROOM_ID;
const userId = window.USER_ID;
const isReadOnly = !!window.CHAT_READ_ONLY;


if (isReadOnly && typeof applyReadOnlyUI === "function") {
  applyReadOnlyUI();
}

/* ===== WebSocket ===== */
const WS_BASE = (location.protocol === "https:" ? "wss://" : "ws://") + location.host;
const socket = new WebSocket(`${WS_BASE}/ws?userId=${userId}`);

const stompClient = Stomp.over(socket);
stompClient.debug = null;

/* ===== 状態 ===== */
const messageMap = new Map();
let lastSenderId = null;


function resetChatState() {
  document.getElementById("chatMessages").innerHTML = ""; // 화면 비우기
  messageMap.clear();
  lastSenderId = null;
}

document.addEventListener("DOMContentLoaded", async () => {
  resetChatState();
  await loadHistory(0);
});


/* ===== STOMP 연결 ===== */
stompClient.connect(
  {},
  () => {
    console.log("CONNECTED");

    fetch(`/api/chat/rooms/${roomId}/read?userId=${userId}`, {
      method: "POST"
    });


    stompClient.subscribe("/topic/chat/" + roomId, (frame) => {
      const data = JSON.parse(frame.body);

      appendMessage(data);

      if (data.senderId !== userId) {
        fetch(`/api/chat/rooms/${roomId}/read?userId=${userId}`, {
          method: "POST"
        });
      }
    });
  },
  (err) => {
    console.error("STOMP ERROR", err);
  }
);

/* ===== 메市지 전송 ===== */
function sendMessage() {
  const input = document.getElementById("messageInput");
  const text = input.value.trim();


  if (isReadOnly) {
    alert("旅行終了後1週間を過ぎたため、チャットは閲覧のみ可能です。");
    return;
  }
  if (!text) return;

  stompClient.send(
    "/app/chat.send/" + roomId,
    {},
    JSON.stringify({ originalText: text })
  );

  input.value = "";
}

/* ===== 메市지 렌더링 ===== */
function appendMessage(message) {
  const chat = document.getElementById("chatMessages");

  const isMe = message.senderId === userId;
  const isContinued = lastSenderId === message.senderId;

  const wrapper = document.createElement("div");
  wrapper.className = `chat-message ${isMe ? "me" : "other"} ${isContinued ? "continued" : ""}`;

if (!isMe) {
  const profile = document.createElement("div");
  profile.className = "profile";

  const imageUrl = message.senderProfileImageUrl || "/images/default_profile.png";

  profile.innerHTML = `<img src="${imageUrl}" />`;

  if (isContinued) profile.style.visibility = "hidden";
  wrapper.appendChild(profile);
}

  const messageArea = document.createElement("div");
  messageArea.className = "message-area";

  if (!isMe && !isContinued) {
    const sender = document.createElement("div");
    sender.className = "sender";
    sender.innerText = message.senderNickname;
    messageArea.appendChild(sender);
  }

  const bubbleRow = document.createElement("div");
  bubbleRow.className = `bubble-row ${isMe ? "me" : ""}`;

  const bubble = document.createElement("div");
  bubble.className = `bubble message-text ${isMe ? "yellow" : "white"}`;
  bubble.innerText = message.originalText;

  const time = document.createElement("span");
  time.className = "time";
  time.innerText = formatTime(message.createdAt);

  if (isMe) {
    bubbleRow.appendChild(time);
    bubbleRow.appendChild(bubble);
  } else {
    bubbleRow.appendChild(bubble);
    bubbleRow.appendChild(time);
  }

  messageArea.appendChild(bubbleRow);

  const translateArea = document.createElement("div");
  translateArea.className = `translate-area ${isMe ? "right" : ""}`;

  const translateBtn = document.createElement("button");
  translateBtn.className = "translate-btn";
  translateBtn.innerText = "翻訳";
  translateBtn.dataset.messageId = message.messageId;

  translateArea.appendChild(translateBtn);
  messageArea.appendChild(translateArea);

  wrapper.appendChild(messageArea);
  chat.appendChild(wrapper);

  lastSenderId = message.senderId;
  messageMap.set(message.messageId, wrapper);
}

/* ===== 翻訳 ===== */
const AVAILABLE_LANGUAGES = [
  { code: "ko", label: "韓国語" },
  { code: "ja", label: "日本語" },
  { code: "en", label: "English" }
];

document.getElementById("chatMessages").addEventListener("click", (e) => {
  if (!e.target.classList.contains("translate-btn")) return;

  const messageId = Number(e.target.dataset.messageId);
  const wrapper = messageMap.get(messageId);
  if (!wrapper) return;


    if (isReadOnly) {
      alert("旅行終了後1週間を過ぎたため、チャットは閲覧のみ可能です。");
      return;
    }
  showLanguageSelect(wrapper, messageId);
});

function showLanguageSelect(wrapper, messageId) {
  const translateArea = wrapper.querySelector(".translate-area");
  translateArea.innerHTML = "";

  const select = document.createElement("select");
  select.className = "translate-select";
  select.innerHTML = `<option disabled selected>言語 選択</option>`;

  AVAILABLE_LANGUAGES.forEach(lang => {
    const option = document.createElement("option");
    option.value = lang.code;
    option.innerText = lang.label;
    select.appendChild(option);
  });

  select.onchange = () => {
    translateMessage(messageId, select.value);
    select.innerHTML = `<option>翻訳中...</option>`;
    select.disabled = true;
  };

  translateArea.appendChild(select);
}

function translateMessage(messageId, targetLanguage) {
  fetch(`/api/chat/messages/${messageId}/translate?targetLanguage=${targetLanguage}`, {
    method: "POST"
  })
    .then(res => res.json())
    .then(updateTranslatedMessage)
    .catch(console.error);
}

function updateTranslatedMessage(message) {
  const wrapper = messageMap.get(message.messageId);
  if (!wrapper) return;

  wrapper.querySelector(".message-text").innerText = message.translatedText;

  const translateArea = wrapper.querySelector(".translate-area");
  translateArea.innerHTML = "";

  const btn = document.createElement("button");
  btn.className = "translate-btn";
  btn.innerText = "翻訳";
  btn.dataset.messageId = message.messageId;

  translateArea.appendChild(btn);
}

/* ===== その他 ===== */
function formatTime(dateString) {
  const date = new Date(dateString);
  let h = date.getHours();
  const m = date.getMinutes().toString().padStart(2, "0");
  const period = h >= 12 ? "午後" : "午前";
  h = h % 12 || 12;
  return `${period} ${h}:${m}`;
}

async function loadHistory(page = 0) {
  try {
    const res = await fetch(`/api/chat/rooms/${roomId}/messages?page=${page}&size=20`);
    if (!res.ok) {
      console.error("HISTORY LOAD FAIL", res.status);
      return;
    }

    const data = await res.json();

    if (page === 0) {
      const chatEl = document.getElementById("chatMessages");
      if (chatEl) chatEl.innerHTML = "";
      messageMap.clear();
      lastSenderId = null;
    }

    (data.content ?? []).slice().reverse().forEach(appendMessage);
  } catch (e) {
    console.error("HISTORY LOAD ERROR", e);
  }
}


document.addEventListener("DOMContentLoaded", () => {
  const input = document.getElementById("messageInput");
  if (!input) return;

  input.addEventListener("keydown", (e) => {
    // IME(한글/日本語) 조합 중 Enter는 전송하면 안 됨
    if (e.isComposing || e.keyCode === 229) return;

    if (e.key === "Enter") {
      if (e.shiftKey) {
        // Shift+Enter: 줄바꿈 허용 (textarea 기본 동작)
        return;
      }
      // Enter: 전송
      e.preventDefault();
      sendMessage();
    }
  });
});

async function leaveRoom() {

  const res = await fetch(`/api/chat/rooms/${roomId}/leave?userId=${userId}`, { method: "POST" });

  if (res.status === 403) {
    alert("退出する権限がありません。（読み取り専用または認証不一致）");
    return;
  }
  if (!res.ok) {
    alert("退出に失敗しました。");
    return;
  }


  if (window.parent && window.parent !== window) {
    window.parent.postMessage({ type: "CHAT_LEFT", roomId }, "*");
  }

  // 모달이 아닌 일반 페이지라면 一覧へ
  location.href = `/chat/rooms?userId=${userId}`;
}


