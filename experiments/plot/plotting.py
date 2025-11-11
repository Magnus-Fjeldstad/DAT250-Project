import json
import matplotlib.pyplot as plt

with open("results.json", "r") as f:
    data = json.load(f)

svelte_page = [d["time"] for d in data if d["framework"] == "Svelte" and d["type"] == "page_load"]
svelte_render = [d["time"] for d in data if d["framework"] == "Svelte" and d["type"] == "render_10000"]
vue_page = [d["time"] for d in data if d["framework"] == "Vue" and d["type"] == "page_load"]
vue_render = [d["time"] for d in data if d["framework"] == "Vue" and d["type"] == "render_10000"]

plt.figure(figsize=(8,6))
plt.boxplot([svelte_page, vue_page, svelte_render, vue_render],
            labels=["Svelte Page Load", "Vue Page Load", "Svelte Render 10k", "Vue Render 10k"])
plt.ylabel("Time (ms)")
plt.title("Vue vs Svelte Performance")
plt.grid(axis="y", linestyle="--", alpha=0.7)
plt.show()
