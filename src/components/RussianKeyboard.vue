<script setup lang="ts">
/*
 * Russian virtual keyboard component.
 * Standard ЙЦУКЕН layout with 4 rows. Emits keystrokes to parent.
 */
defineProps<{
  disabled: boolean
}>()

const emit = defineEmits<{
  input: [char: string]
  backspace: []
  enter: []
}>()

const rows = [
  ['й', 'ц', 'у', 'к', 'е', 'н', 'г', 'ш', 'щ', 'з', 'х', 'ъ'],
  ['ф', 'ы', 'в', 'а', 'п', 'р', 'о', 'л', 'д', 'ж', 'э'],
  ['я', 'ч', 'с', 'м', 'и', 'т', 'ь', 'б', 'ю', '.'],
] as const

function pressKey(key: string): void {
  emit('input', key)
}

function pressBackspace(): void {
  emit('backspace')
}

function pressEnter(): void {
  emit('enter')
}
</script>

<template>
  <div class="rus-keyboard" :class="{ 'rus-keyboard--disabled': disabled }">
    <!-- Row 1 -->
    <div class="rus-keyboard__row">
      <button
        v-for="key in rows[0]"
        :key="key"
        class="rus-key rus-key--letter"
        :disabled="disabled"
        @click="pressKey(key)"
      >
        {{ key }}
      </button>
      <button class="rus-key rus-key--special" :disabled="disabled" @click="pressBackspace">
        ⌫
      </button>
    </div>

    <!-- Row 2 -->
    <div class="rus-keyboard__row">
      <button
        v-for="key in rows[1]"
        :key="key"
        class="rus-key rus-key--letter"
        :disabled="disabled"
        @click="pressKey(key)"
      >
        {{ key }}
      </button>
      <button class="rus-key rus-key--special" :disabled="disabled" @click="pressEnter">↵</button>
    </div>

    <!-- Row 3 -->
    <div class="rus-keyboard__row">
      <button
        v-for="key in rows[2]"
        :key="key"
        class="rus-key rus-key--letter"
        :disabled="disabled"
        @click="pressKey(key)"
      >
        {{ key }}
      </button>
      <button class="rus-key rus-key--special" :disabled="disabled" @click="pressBackspace">
        ⌫
      </button>
    </div>

    <!-- Bottom row: space + enter -->
    <div class="rus-keyboard__row rus-keyboard__row--bottom">
      <button class="rus-key rus-key--space" :disabled="disabled" @click="pressKey(' ')">
        Space
      </button>
      <button class="rus-key rus-key--enter" :disabled="disabled" @click="pressEnter">
        ↵ Enter
      </button>
    </div>
  </div>
</template>

<style scoped>
.rus-keyboard {
  max-width: 480px;
  margin: 16px auto 0;
  user-select: none;
}

.rus-keyboard--disabled {
  opacity: 0.5;
  pointer-events: none;
}

.rus-keyboard__row {
  display: flex;
  gap: 4px;
  margin-bottom: 4px;
  justify-content: center;
}

.rus-keyboard__row--bottom {
  gap: 8px;
}

.rus-key {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 34px;
  height: 40px;
  padding: 0 6px;
  border: 1px solid var(--oj-card-border);
  border-radius: 4px;
  background: #fff;
  font-size: 15px;
  font-family: 'Segoe UI', 'Times New Roman', serif;
  cursor: pointer;
  transition:
    background 0.1s,
    transform 0.05s;
  color: var(--oj-text);
  line-height: 1;
}

.rus-key:hover:not(:disabled) {
  background: #e8f0fe;
  border-color: var(--oj-primary);
}

.rus-key:active:not(:disabled) {
  background: var(--oj-primary);
  color: #fff;
  transform: scale(0.94);
}

.rus-key:disabled {
  cursor: default;
  color: var(--oj-text-muted);
}

.rus-key--letter {
  flex: 0 0 auto;
}

.rus-key--special {
  min-width: 46px;
  font-size: 14px;
  color: var(--oj-text-secondary);
}

.rus-key--space {
  flex: 1;
  max-width: 200px;
  font-size: 13px;
  color: var(--oj-text-muted);
}

.rus-key--enter {
  min-width: 70px;
  font-size: 13px;
  background: var(--oj-primary);
  color: #fff;
  border-color: var(--oj-primary);
}

.rus-key--enter:hover:not(:disabled) {
  background: var(--oj-primary-hover);
}
</style>
