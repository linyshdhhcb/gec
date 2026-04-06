<template>
  <div class="ai-qa-container">
    <!-- 页面标题和logo -->
    <div class="header">
      <div class="logo-container">
        <div class="logo-placeholder">🌊</div>
        <div class="title-container">
          <h1 class="main-title">海洋知识AI问答</h1>
          <p class="subtitle">探索海洋奥秘，AI实时解答</p>
        </div>
      </div>
      <div class="header-actions">
        <button class="clear-btn" @click="clearChat" title="清空对话">
          <i class="icon-clear"></i> 清空对话
        </button>
      </div>
    </div>

    <!-- 主聊天区域 -->
    <div class="main-content">
      <!-- 聊天消息容器 -->
      <div ref="chatContainer" class="chat-container">
        <!-- 欢迎消息 -->
        <div class="welcome-section" v-if="messages.length <= 1">
          <div class="welcome-card">
            <div class="welcome-icon">
              <i class="icon-welcome"></i>
            </div>
            <h3>欢迎使用海洋知识AI助手</h3>
            <p>我是您的专属海洋知识顾问，可以解答各种海洋相关问题</p>
            <div class="quick-questions">
              <p>试试问我：</p>
              <div class="question-chips">
                <span 
                  v-for="(question, index) in quickQuestions" 
                  :key="index" 
                  class="question-chip"
                  @click="useQuickQuestion(question)"
                >
                  {{ question }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- 消息列表 -->
        <div class="messages-list">
          <div 
            v-for="(msg, index) in messages" 
            :key="index" 
            class="message-item"
            :class="{ 
              'user-message': msg.type === 'user', 
              'ai-message': msg.type === 'ai',
              'streaming': msg.isStreaming 
            }"
          >
            <!-- AI消息 -->
            <div v-if="msg.type === 'ai'" class="message-content ai">
              <div class="message-header">
                <div class="avatar ai-avatar">
                  <span class="avatar-icon">🤖</span>
                </div>
                <span class="sender-name">海洋AI助手</span>
                <span class="message-time">{{ msg.time }}</span>
              </div>
              <div class="message-body">
                <div class="text-content">{{ msg.text }}</div>
                <!-- 流式输出光标 -->
                <div v-if="msg.isStreaming" class="streaming-indicator">
                  <div class="typing-dots">
                    <div class="dot"></div>
                    <div class="dot"></div>
                    <div class="dot"></div>
                  </div>
                </div>
              </div>
              <div class="message-footer" v-if="!msg.isStreaming && msg.text">
                <button 
                  class="action-btn copy-btn" 
                  @click="copyToClipboard(msg.text)"
                  title="复制内容"
                >
                  <i class="icon-copy"></i>
                </button>
              </div>
            </div>

            <!-- 用户消息 -->
            <div v-if="msg.type === 'user'" class="message-content user">
              <div class="message-header">
                <span class="message-time">{{ msg.time }}</span>
                <span class="sender-name">您</span>
                <div class="avatar user-avatar">
                  <span class="avatar-icon">👤</span>
                </div>
              </div>
              <div class="message-body">
                <div class="text-content">{{ msg.text }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="input-section">
          <div class="input-container">
            <div class="input-wrapper">
              <textarea
                ref="inputField"
                v-model="userInput"
                placeholder="输入您想了解的海洋知识问题..."
                rows="1"
                @input="adjustTextareaHeight"
                @keydown.enter.exact.prevent="sendMessage"
                @keydown.enter.shift.exact="userInput += '\n'"
                :disabled="isLoading"
                class="message-input"
              ></textarea>
              <div class="input-actions">
                <button
                  v-if="isLoading"
                  class="stop-btn"
                  @click="stopGeneration"
                >
                  <i class="icon-stop"></i> 停止生成
                </button>
                <button
                  class="send-btn"
                  @click="sendMessage"
                  :disabled="!userInput.trim() || isLoading"
                  :class="{ disabled: !userInput.trim() || isLoading }"
                >
                  <i class="icon-send"></i>
                  <span v-if="isLoading">思考中...</span>
                  <span v-else>发送</span>
                </button>
              </div>
            </div>
            <div class="input-footer">
              <div class="tips">
                <i class="icon-tip"></i>
                <span>按 Enter 发送，Shift + Enter 换行</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 错误提示 -->
    <transition name="fade">
      <div v-if="errorMessage" class="error-toast">
        <i class="icon-error"></i>
        <span>{{ errorMessage }}</span>
        <button class="close-error" @click="errorMessage = ''">×</button>
      </div>
    </transition>

    <!-- 复制成功提示 -->
    <transition name="fade">
      <div v-if="showCopySuccess" class="copy-toast">
        <i class="icon-success"></i>
        <span>已复制到剪贴板</span>
      </div>
    </transition>
  </div>
</template>

<script>
export default {
  name: 'OceanAIQA',
  
  data() {
    return {
      userInput: '',
      messages: [
        {
          text: '您好！我是海洋知识AI助手，专注解答海洋科学、生物、地理、环保等相关问题。请问有什么可以帮您？',
          type: 'ai',
          time: this.formatTime(new Date()),
          isStreaming: false
        }
      ],
      isLoading: false,
      errorMessage: '',
      showCopySuccess: false,
      abortController: null,
      quickQuestions: [
        '海豚为什么会跃出水面？',
        '珊瑚白化的主要原因是什么？',
        '海洋是如何调节地球气候的？',
        '世界上最深的海洋是哪里？',
        '如何保护海洋生态环境？'
      ]
    };
  },
  
  mounted() {
    this.$nextTick(() => {
      this.scrollToBottom();
      this.$refs.inputField?.focus();
    });
  },
  
  methods: {
    formatTime(date) {
      const hours = date.getHours().toString().padStart(2, '0');
      const minutes = date.getMinutes().toString().padStart(2, '0');
      return `${hours}:${minutes}`;
    },
    
    adjustTextareaHeight() {
      const textarea = this.$refs.inputField;
      textarea.style.height = 'auto';
      textarea.style.height = Math.min(textarea.scrollHeight, 120) + 'px';
    },
    
    async sendMessage() {
      const question = this.userInput.trim();
      if (!question || this.isLoading) return;
      
      this.messages.push({
        text: question,
        type: 'user',
        time: this.formatTime(new Date()),
        isStreaming: false
      });
      
      this.userInput = '';
      this.adjustTextareaHeight();
      this.scrollToBottom();
      
      const aiMessageIndex = this.messages.length;
      this.messages.push({
        text: '',
        type: 'ai',
        time: this.formatTime(new Date()),
        isStreaming: true
      });
      
      this.isLoading = true;
      this.errorMessage = '';
      
      if (this.abortController) {
        this.abortController.abort();
      }
      
      this.abortController = new AbortController();
      
      try {
        await this.streamAIResponse(question, aiMessageIndex);
      } catch (error) {
        if (error.name !== 'AbortError') {
          console.error('流式请求失败:', error);
          this.errorMessage = error.message || '请求失败，请稍后重试';
          this.messages[aiMessageIndex] = {
            text: '抱歉，回答问题失败。请检查网络连接或稍后重试。',
            type: 'ai',
            time: this.formatTime(new Date()),
            isStreaming: false
          };
        }
      } finally {
        this.isLoading = false;
        this.scrollToBottom();
      }
    },
    
    async streamAIResponse(question, messageIndex) {
      try {
        const API_URL = 'http://localhost:8899/ai/chatstream';
        
        const response = await fetch(`${API_URL}?message=${encodeURIComponent(question)}&chatId=${Date.now()}`, {
          method: 'GET',
          headers: {
            'Accept': 'text/html',
            'Cache-Control': 'no-cache'
          },
          signal: this.abortController.signal
        });
        
        if (!response.ok) {
          throw new Error(`HTTP错误: ${response.status}`);
        }
        
        const reader = response.body.getReader();
        const decoder = new TextDecoder('utf-8');
        let accumulatedText = '';
        
        while (true) {
          const { done, value } = await reader.read();
          if (done) break;
          
          const chunk = decoder.decode(value, { stream: true });
          
          // 直接拼接文本内容
          accumulatedText += chunk;
          this.updateMessageText(messageIndex, accumulatedText);
        }
        
        // 完成流式输出
        this.messages[messageIndex].isStreaming = false;
        this.messages[messageIndex].time = this.formatTime(new Date());
        
      } catch (error) {
        if (error.name !== 'AbortError') {
          throw error;
        }
      }
    },
    
    updateMessageText(index, text) {
      if (this.messages[index]) {
        this.messages[index].text = text;
        this.$forceUpdate();
        this.scrollToBottom();
      }
    },
    
    stopGeneration() {
      if (this.abortController) {
        this.abortController.abort();
        this.isLoading = false;
        
        const streamingMessage = this.messages.find(msg => msg.isStreaming);
        if (streamingMessage) {
          streamingMessage.isStreaming = false;
          streamingMessage.time = this.formatTime(new Date());
        }
      }
    },
    
    async copyToClipboard(text) {
      try {
        await navigator.clipboard.writeText(text);
        this.showCopySuccess = true;
        setTimeout(() => {
          this.showCopySuccess = false;
        }, 2000);
      } catch (err) {
        console.error('复制失败:', err);
        this.errorMessage = '复制失败，请手动复制';
      }
    },
    
    useQuickQuestion(question) {
      this.userInput = question;
      this.$nextTick(() => {
        this.adjustTextareaHeight();
        this.$refs.inputField?.focus();
      });
    },
    
    clearChat() {
      if (this.isLoading) {
        this.stopGeneration();
      }
      
      this.messages = [{
        text: '您好！我是海洋知识AI助手，专注解答海洋科学、生物、地理、环保等相关问题。请问有什么可以帮您？',
        type: 'ai',
        time: this.formatTime(new Date()),
        isStreaming: false
      }];
      this.userInput = '';
      this.errorMessage = '';
      this.adjustTextareaHeight();
    },
    
    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.chatContainer;
        if (container) {
          container.scrollTop = container.scrollHeight;
        }
      });
    }
  },
  
  watch: {
    messages: {
      handler() {
        this.scrollToBottom();
      },
      deep: true
    }
  }
};
</script>

<style scoped>
.ai-qa-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.header {
  padding: 20px 40px;
  background: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 20px;
}

.logo-placeholder {
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  background: linear-gradient(135deg, #1976d2, #42a5f5);
  border-radius: 8px;
}

.title-container {
  display: flex;
  flex-direction: column;
}

.main-title {
  margin: 0;
  font-size: 24px;
  color: #1976d2;
  font-weight: 600;
}

.subtitle {
  margin: 5px 0 0 0;
  font-size: 14px;
  color: #666;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.clear-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  color: #666;
}

.clear-btn:hover {
  background: #f5f5f5;
  border-color: #1976d2;
  color: #1976d2;
}

.main-content {
  flex: 1;
  overflow: hidden;
  padding: 20px 40px;
}

.chat-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.welcome-section {
  padding: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.welcome-card {
  text-align: center;
  max-width: 600px;
}

.welcome-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  background: linear-gradient(135deg, #1976d2, #42a5f5);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.welcome-card h3 {
  margin: 0 0 10px 0;
  color: #1976d2;
  font-size: 22px;
}

.welcome-card p {
  margin: 0 0 20px 0;
  color: #666;
  font-size: 14px;
}

.quick-questions {
  margin-top: 20px;
}

.quick-questions p {
  margin: 0 0 10px 0;
  color: #999;
  font-size: 13px;
}

.question-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
}

.question-chip {
  padding: 8px 16px;
  background: #e3f2fd;
  color: #1976d2;
  border-radius: 20px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
}

.question-chip:hover {
  background: #1976d2;
  color: white;
}

.messages-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.message-item {
  margin-bottom: 20px;
}

.message-content {
  max-width: 70%;
  padding: 15px;
  border-radius: 12px;
  position: relative;
}

.message-content.ai {
  background: #f5f5f5;
  margin-right: auto;
}

.message-content.user {
  background: linear-gradient(135deg, #1976d2, #42a5f5);
  color: white;
  margin-left: auto;
}

.message-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
  font-size: 12px;
}

.message-content.user .message-header {
  flex-direction: row-reverse;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e0e0e0;
}

.avatar-icon {
  font-size: 18px;
}

.sender-name {
  font-weight: 500;
}

.message-time {
  color: #999;
  font-size: 11px;
}

.message-content.user .message-time {
  color: rgba(255, 255, 255, 0.7);
}

.message-body {
  line-height: 1.6;
}

.text-content {
  white-space: pre-wrap;
  word-break: break-word;
}

.streaming-indicator {
  margin-top: 10px;
}

.typing-dots {
  display: flex;
  gap: 4px;
}

.dot {
  width: 8px;
  height: 8px;
  background: #1976d2;
  border-radius: 50%;
  animation: typing 1.4s infinite;
}

.dot:nth-child(2) {
  animation-delay: 0.2s;
}

.dot:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: 0.7;
  }
  30% {
    transform: translateY(-10px);
    opacity: 1;
  }
}

.message-footer {
  margin-top: 10px;
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 4px 8px;
  border: none;
  background: transparent;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s;
  color: #666;
}

.action-btn:hover {
  background: rgba(0, 0, 0, 0.05);
}

.input-section {
  padding: 20px;
  background: #fafafa;
  border-top: 1px solid #eee;
}

.input-container {
  max-width: 900px;
  margin: 0 auto;
}

.input-wrapper {
  display: flex;
  gap: 10px;
  align-items: flex-end;
}

.message-input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  resize: none;
  font-family: inherit;
  font-size: 14px;
  line-height: 1.5;
  transition: all 0.3s;
  max-height: 120px;
}

.message-input:focus {
  outline: none;
  border-color: #1976d2;
  box-shadow: 0 0 0 3px rgba(25, 118, 210, 0.1);
}

.message-input:disabled {
  background: #f5f5f5;
  cursor: not-allowed;
}

.input-actions {
  display: flex;
  gap: 10px;
}

.send-btn, .stop-btn {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.send-btn {
  background: linear-gradient(135deg, #1976d2, #42a5f5);
  color: white;
}

.send-btn:hover:not(.disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(25, 118, 210, 0.3);
}

.send-btn.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.stop-btn {
  background: #ff5252;
  color: white;
}

.stop-btn:hover {
  background: #ff1744;
}

.input-footer {
  margin-top: 10px;
  text-align: center;
}

.tips {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: #999;
}

.error-toast, .copy-toast {
  position: fixed;
  top: 20px;
  right: 20px;
  padding: 12px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  display: flex;
  align-items: center;
  gap: 10px;
  z-index: 1000;
}

.error-toast {
  border-left: 4px solid #ff5252;
  color: #ff5252;
}

.copy-toast {
  border-left: 4px solid #4caf50;
  color: #4caf50;
}

.close-error {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  color: #999;
  padding: 0;
  margin-left: 10px;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter, .fade-leave-to {
  opacity: 0;
}

/* 滚动条样式 */
.messages-list::-webkit-scrollbar {
  width: 6px;
}

.messages-list::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.messages-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.messages-list::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}
</style>
