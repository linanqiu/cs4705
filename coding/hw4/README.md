```
2226 2459 0.905246034974
```

```
2303 2459 0.936559577064
```

Using the following additional features

- Current Word `w_i`
- Previous Word `w_{i-1}`
- Word Two Back `w_{i-2`
- Next Word `w_{i+1}`
- Next Two Ahead `w_{i+2}`

```
2322 2459 0.944286295242
```

Using the following additional features

- Current Word `w_i`
- Previous Word `w_{i-1}`
- Word Two Back `w_{i-2`
- Next Word `w_{i+1}`
- Next Two Ahead `w_{i+2}`
- Word Bigram 1 `w_{i-2}, w_{i-1}`
- Word Bigram 2 `w_{i-1}, w_i`
- Word Bigram 3 `w_i, w_{i+1}`
- Word Bigram 4 `w_{i+1}, w_{i+2}`

```
2335 2459 0.949572997153
```

Using the following additional features

- Current Word `w_i`
- Previous Word `w_{i-1}`
- Word Two Back `w_{i-2`
- Next Word `w_{i+1}`
- Next Two Ahead `w_{i+2}`
- Word Bigram 1 `w_{i-2}, w_{i-1}`
- Word Bigram 2 `w_{i-1}, w_i`
- Word Bigram 3 `w_i, w_{i+1}`
- Word Bigram 4 `w_{i+1}, w_{i+2}`
- Prefixes of length 4 or less (originally used 3. It reduced score. Using 4 increased score)

```
2338 2459 0.950793005287
```

Using word buckets

```
2345 2459 0.953639690931
```

