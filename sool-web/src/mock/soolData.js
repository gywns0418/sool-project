export const categories = [
  { id: 1, emoji: '🍷', name: 'WINE'},
  { id: 2, emoji: '🥃', name: 'WHISKY'},
  { id: 3, emoji: '🍺', name: 'BEER'},
  { id: 4, emoji: '🍶', name: 'TRADITIONAL'},
  { id: 5, emoji: '🍸', name: 'COCKTAIL'}
]

export const comments = [
  {
    id: 1,
    authorInitial: 'L',
    author: 'lee_cellar',
    date: '1시간 전',
    text: '향 표현이 정말 좋네요. 다음에 마실 때 참고해볼게요.',
    likes: 3,
    replies: [
      {
        id: 11,
        authorInitial: 'K',
        author: 'kimwine',
        date: '30분 전',
        text: '감사합니다. 다음에는 빈티지 차이도 같이 적어보려고요.',
        likes: 1
      }
    ]
  },
  {
    id: 2,
    authorInitial: 'P',
    author: 'park_sool',
    date: '어제',
    text: '혹시 디캔팅 시간은 얼마나 가져가셨나요?',
    likes: 2,
    replies: []
  }
]
