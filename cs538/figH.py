import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
from scipy.signal import savgol_filter

x = np.linspace(0, 10, 1000)
y = np.array([np.sin(val**2) for val in x])
y = y + np.random.rand(1000)
y_filt = savgol_filter(y, 61, 1)
y_filt2 = savgol_filter(y, 61, 5)

sns.set()
fig = plt.figure()
ax = fig.add_subplot(111, frameon=False)
ax1 = fig.add_subplot(311)
ax2 = fig.add_subplot(312)
ax3 = fig.add_subplot(313)
sns.lineplot(x, y, ax=ax1, label='(a)')
sns.lineplot(x, y_filt, ax=ax2, label='(b)')
sns.lineplot(x, y_filt2, ax=ax3, label='(c)')
ax1.legend(handlelength=0, frameon=False)
ax2.legend(handlelength=0, frameon=False)
ax3.legend(handlelength=0, frameon=False)

ax.set_xlabel('Frequency')
ax.set_ylabel('Amplitude')
ax.set_xticklabels('')
ax.set_yticklabels('')

ax1.set_xlim(0, 10)
ax1.set_xticklabels('')
ax1.set_yticklabels('')
ax2.set_xlim(0, 10)
ax2.set_xticklabels('')
ax2.set_yticklabels('')
ax3.set_xlim(0, 10)
ax3.set_xticklabels('')
ax3.set_yticklabels('')

plt.savefig("savgol2.png")
#plt.show()
