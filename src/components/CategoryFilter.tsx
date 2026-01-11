import React from 'react';
import { cn } from '@/lib/utils';

interface CategoryFilterProps {
  selectedCategory: string;
  onCategoryChange: (category: string) => void;
}

const categories = ['All', 'Tech', 'Cultural', 'Sports', 'Workshop'];

const CategoryFilter: React.FC<CategoryFilterProps> = ({ selectedCategory, onCategoryChange }) => {
  return (
    <div className="flex flex-wrap gap-2">
      {categories.map((category) => (
        <button
          key={category}
          onClick={() => onCategoryChange(category)}
          className={cn(
            'px-4 py-2 rounded-full text-sm font-medium transition-all duration-200',
            selectedCategory === category
              ? 'gradient-primary text-primary-foreground shadow-md'
              : 'bg-card text-muted-foreground hover:text-foreground hover:bg-muted border border-border'
          )}
        >
          {category}
        </button>
      ))}
    </div>
  );
};

export default CategoryFilter;
