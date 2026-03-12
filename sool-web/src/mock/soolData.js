export const categories = [
  { id: 1, emoji: '🍷', name: '와인', count: '124종' },
  { id: 2, emoji: '🥃', name: '위스키', count: '98종' },
  { id: 3, emoji: '🍺', name: '맥주', count: '86종' },
  { id: 4, emoji: '🍶', name: '전통주', count: '54종' },
  { id: 5, emoji: '🍸', name: '칵테일', count: '38종' }
]

export const popularDrinks = [
  { id: 1, emoji: '🍷', rank: 1, gold: true, category: '레드 와인', name: 'Château Margaux', rating: '★★★★★ 4.9', likes: 128 },
  { id: 2, emoji: '🥃', rank: 2, category: '싱글 몰트', name: 'Glenfiddich 18Y', rating: '★★★★☆ 4.6', likes: 97 },
  { id: 3, emoji: '🍺', rank: 3, category: '트라피스트 에일', name: 'Westvleteren 12', rating: '★★★★★ 4.8', likes: 84 },
  { id: 4, emoji: '🍶', rank: 4, category: '증류 전통주', name: '화요 41', rating: '★★★★☆ 4.4', likes: 61 }
]

export const recentNotes = [
  {
    id: 1,
    emoji: '🍷',
    category: '레드 와인',
    drinkName: 'Château Margaux 2018',
    stars: '★★★★★',
    text: '잔에 따르자마자 올라오는 블랙커런트와 시가박스 향이 정말 인상적이었다. 타닌이 매우 부드럽고 세련되어 있어 지금 당장 마시기에도 충분히 즐거운 와인.',
    authorInitial: 'K',
    author: 'kimwine',
    date: '오늘',
    avatarClass: ''
  },
  {
    id: 2,
    emoji: '🥃',
    category: '싱글 몰트',
    drinkName: 'Glenfiddich 18Y',
    stars: '★★★★☆',
    text: '꿀과 배 향이 우아하게 어우러지며, 오크의 바닐라 향이 은은하게 뒷받침한다. 18년 숙성답게 부드럽고 균형 잡힌 피니시가 오래 여운을 남긴다.',
    authorInitial: 'J',
    author: 'jwhisky',
    date: '2시간 전',
    avatarClass: 'av2'
  },
  {
    id: 3,
    emoji: '🍶',
    category: '증류 전통주',
    drinkName: '화요 41',
    stars: '★★★★☆',
    text: '처음엔 강한 도수가 느껴지지만 곧 부드러운 쌀 향이 퍼지며 균형이 잡힌다. 깔끔한 목 넘김과 길게 이어지는 여운이 인상적. 한식 페어링에 훌륭하다.',
    authorInitial: 'P',
    author: 'park_sool',
    date: '어제',
    avatarClass: 'av3'
  }
]

export const drinkGrid = [
  { id: 1, emoji: '🍷', category: '레드 와인', name: 'Château Margaux 2018', meta: '13.5% · 프랑스', rating: '★★★★★ 4.9', likes: 128, liked: true },
  { id: 2, emoji: '🍾', category: '샴페인', name: 'Dom Pérignon 2012', meta: '12.5% · 프랑스', rating: '★★★★☆ 4.7', likes: 94, liked: false },
  { id: 3, emoji: '🍷', category: '레드 와인', name: 'Opus One 2019', meta: '14.5% · 미국', rating: '★★★★☆ 4.5', likes: 77, liked: false },
  { id: 4, emoji: '🥂', category: '화이트 와인', name: 'Puligny-Montrachet', meta: '13% · 프랑스', rating: '★★★★☆ 4.4', likes: 52, liked: false },
  { id: 5, emoji: '🍷', category: '레드 와인', name: 'Sassicaia 2017', meta: '14% · 이탈리아', rating: '★★★★★ 4.8', likes: 88, liked: false },
  { id: 6, emoji: '🍾', category: '로제 와인', name: 'Whispering Angel Rosé', meta: '13% · 프랑스', rating: '★★★★☆ 4.2', likes: 63, liked: false }
]

export const flavorProfile = [
  { label: '당도', value: 2, width: '40%' },
  { label: '산도', value: 4, width: '80%' },
  { label: '바디', value: 5, width: '100%' },
  { label: '탄닌', value: 4, width: '80%' }
]

export const ratingDistribution = [
  { label: '★★★★★', value: 34, width: '68%' },
  { label: '★★★★', value: 13, width: '26%' },
  { label: '★★★', value: 1, width: '6%' }
]

export const detailNotes = [
  {
    id: 1,
    authorInitial: '김',
    author: 'kimwine',
    date: '2025. 10. 15',
    stars: '★★★★★',
    text: '잔에 따르자마자 올라오는 블랙커런트와 시가박스 향이 인상적이었다. 타닌이 매우 부드럽고 긴 여운이 인상적. 지금 마셔도 훌륭하지만 5년 이상 셀러링하면 더 진가를 발휘할 것 같은 와인.',
    emoji: '🍷',
    likes: 24,
    avatarClass: ''
  },
  {
    id: 2,
    authorInitial: 'J',
    author: 'julie_vin',
    date: '2025. 10. 12',
    stars: '★★★★☆',
    text: '첫 향에서는 잘 익은 베리와 은은한 삼나무 향이 느껴졌다. 산도와 타닌의 밸런스가 좋아서 음식과 함께 마시기 좋았고, 피니시가 길고 단정하다.',
    emoji: '🍷',
    likes: 12,
    avatarClass: 'a2'
  }
]

export const myNotes = [
  { id: 1, emoji: '🍷', category: '레드 와인', title: 'Château Margaux 2018', date: '2026. 03. 11' },
  { id: 2, emoji: '🥃', category: '싱글 몰트', title: 'Glenfiddich 18Y', date: '2026. 03. 09' },
  { id: 3, emoji: '🍶', category: '증류 전통주', title: '화요 41', date: '2026. 03. 05' },
  { id: 4, emoji: '🍺', category: '트라피스트 에일', title: 'Westvleteren 12', date: '2026. 03. 01' }
]

export const noteDetailFlavor = [
  { label: '당도', value: 2, width: '40%' },
  { label: '산도', value: 4, width: '80%' },
  { label: '바디', value: 5, width: '100%' },
  { label: '탄닌', value: 4, width: '80%' }
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
