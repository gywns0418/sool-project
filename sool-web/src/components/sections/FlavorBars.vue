<template>
  <div class="flavor-block">
    <h4>{{ title }}</h4>
    <div v-for="item in items" :key="item.label" class="flavor-row">
      <span class="flavor-label" :class="{ yellow: yellow }">{{ item.label }}</span>
      <div class="flavor-bar-bg">
        <div class="flavor-bar-fill" :class="{ yellow: yellow }" :style="{ width: item.width }"></div>
      </div>
      <span class="flavor-val">{{ item.value }}</span>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true
  },
  items: {
    type: Array,
    default: () => []
  },
  yellow: {
    type: Boolean,
    default: false
  }
})
</script>

<style scoped>
.flavor-block h4 {
  font-size: 11px;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  color: var(--muted);
  font-weight: 600;
  margin-bottom: 16px;
}

.flavor-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.flavor-label {
  font-size: 12.5px;
  color: var(--sub);
  width: 52px;
  flex-shrink: 0;
}

.flavor-label.yellow {
  color: var(--yellow);
}

.flavor-bar-bg {
  flex: 1;
  height: 6px;
  background: var(--surface);
  border-radius: 3px;
  overflow: hidden;
}

.flavor-bar-fill {
  height: 100%;
  background: var(--point);
  border-radius: 3px;
}

.flavor-bar-fill.yellow {
  background: var(--yellow);
}

.flavor-val {
  font-size: 12px;
  font-weight: 600;
  color: var(--ink);
  width: 18px;
  text-align: right;
}
</style>
