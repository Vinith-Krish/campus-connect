const CATEGORY_LABELS = {
  TECH: 'Tech',
  CULTURAL: 'Cultural',
  SPORTS: 'Sports',
  WORKSHOP: 'Workshop',
};

const CATEGORY_CLASSES = {
  TECH: 'bg-blue-500 text-white',
  CULTURAL: 'bg-rose-500 text-white',
  SPORTS: 'bg-green-500 text-white',
  WORKSHOP: 'bg-amber-500 text-white',
};

export const normalizeCategory = (category) => {
  if (!category) return '';

  const normalized = String(category).trim().toUpperCase();
  return CATEGORY_LABELS[normalized] || String(category).trim();
};

export const getCategoryClassName = (category) => {
  if (!category) return 'bg-muted text-muted-foreground';

  return CATEGORY_CLASSES[String(category).trim().toUpperCase()] || 'bg-muted text-muted-foreground';
};

export const normalizeEvent = (event) => {
  if (!event) return event;

  return {
    ...event,
    category: normalizeCategory(event.category),
  };
};
