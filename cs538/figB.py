import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns

x = np.linspace(0, 10)
y_white = np.array([0.1+noise for val, noise
                    in zip(x, np.random.rand(50)/200)])
y_flicker = np.array([0.5/val+noise for val, noise
                      in zip(x, (np.random.rand(50)/200))])
y_blue = np.array([(val/10)**2+0.02 for val in x+np.random.rand(50)/10])

sns.set()
ax = sns.lineplot(x, y_white, label='white noise')
ax = sns.lineplot(x, y_flicker, label='flicker noise')
ax = sns.lineplot(x, y_blue, label='blue noise')
ax.set_xlabel('Frequency')
ax.set_ylabel('Amplitude')
ax.set_ylim(0, 1)
ax.set_xlim(0, 10)
ax.set_xticklabels('')
plt.savefig("noise_spectrums.png")
#plt.show()
