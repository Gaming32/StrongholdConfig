import tqdm

DISTANCE = 32
SPREAD = 3
COUNT = 1024
# COUNT = 128

spread = SPREAD
ring = 0
stronghold = 0
bar = tqdm.tqdm(total=COUNT)
while stronghold < COUNT:
    dist = 4 * DISTANCE + DISTANCE * ring * 6
    ring += 1
    stronghold += spread
    bar.update(spread)
    spread += 2 * spread // (ring + 1)
    spread = min(spread, COUNT - stronghold)
bar.close()

print('Maximum distance ranges from:', dist * 16, 'to:', int(dist + DISTANCE * 1.25) * 16)
