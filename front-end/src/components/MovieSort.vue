<template>
  <div>
    <ul class="list list-inline">
      <span class="label mr-2">Sort by: </span>
      <li class="list-inline-item" v-for="(item, index) in items" :key="index" >
        <a @click="$emit('selected', index)" href="#">{{ item }}</a>
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  name: "movie-sort",
  data() {
    return {
      items: [
        "Likes",
        "Hates",
        "Date",
      ]
    };
  }
};
</script>

<style lang="scss" scoped>
.label,
.list {
  vertical-align: middle;
}
</style>
