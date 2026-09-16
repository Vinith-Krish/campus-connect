const DEFAULT_ERROR_MESSAGE = 'Something went wrong. Please try again.';

export const getUserFriendlyErrorMessage = (error, fallback = DEFAULT_ERROR_MESSAGE) => {
  const status = error?.response?.status;

  if (status === 400) return 'Please check your input and try again.';
  if (status === 401) return 'Your session has expired. Please sign in again.';
  if (status === 403) return 'You do not have permission to perform this action.';
  if (status === 404) return 'The requested resource could not be found.';
  if (status === 409) return 'This request conflicts with existing data.';
  if (status >= 500) return 'The server is temporarily unavailable. Please try again later.';
  if (!status && error?.request) return 'Unable to reach the server. Please check your connection.';

  return fallback;
};
