import matplotlib.pyplot as plt

vue = []
svelte = []

plt.boxplot([vue, svelte], labels=['Vue', 'Svelte'])
plt.ylabel('Render time (ms)')
plt.title('Component render time comparison')
plt.show()
