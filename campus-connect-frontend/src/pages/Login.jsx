import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { authService } from '../services/authService';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Calendar, Mail, Lock, ArrowRight, Eye, EyeOff, CheckCircle2, RefreshCw } from 'lucide-react';
import { useToast } from '../hooks/use-toast';

const generateCaptcha = () => {
  const isAddition = Math.random() < 0.5;
  let first = Math.floor(Math.random() * 9) + 1;
  let second = Math.floor(Math.random() * 9) + 1;

  if (!isAddition && second > first) {
    [first, second] = [second, first];
  }

  return {
    question: `${first} ${isAddition ? '+' : '-'} ${second} = ?`,
    answer: isAddition ? first + second : first - second,
  };
};

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [captcha, setCaptcha] = useState(() => generateCaptcha());
  const [captchaInput, setCaptchaInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const { toast } = useToast();

  const hasCredentials = Boolean(email.trim() && password.trim());
  const isCaptchaValid = hasCredentials && captchaInput.trim() !== '' && Number(captchaInput) === captcha.answer;

  const from = location.state?.from?.pathname || '/events';

  const refreshCaptcha = () => {
    setCaptcha(generateCaptcha());
    setCaptchaInput('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!email.trim() || !password.trim()) {
      toast({
        title: 'Validation Error',
        description: 'Please fill in all fields',
        variant: 'destructive',
      });
      return;
    }

    if (!isCaptchaValid) {
      toast({
        title: 'Validation Error',
        description: 'Please solve the captcha correctly before signing in',
        variant: 'destructive',
      });
      refreshCaptcha();
      return;
    }

    setIsLoading(true);
    try {
      const loginPayload = {
        email, 
        password,
      };

      const response = await authService.login(loginPayload);
      login(response.token, response.user);
      
      toast({
        title: 'Welcome back!',
        description: 'You have successfully logged in.',
      });
      
      navigate(from, { replace: true });
    } catch (error) {
      toast({
        title: 'Login Failed',
        description: error.response?.data?.message || error.message || 'Invalid email or password. Please try again.',
        variant: 'destructive',
      });
      refreshCaptcha();
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-background flex">
      {/* Left Panel - Form */}
      <div className="flex-1 flex items-center justify-center p-8">
        <div className="w-full max-w-md animate-slide-up">
          <Link to="/" className="inline-flex items-center gap-2 mb-8 group">
            <div className="p-2 rounded-xl gradient-primary">
              <Calendar className="h-5 w-5 text-primary-foreground" />
            </div>
            <span className="font-display text-xl font-bold text-foreground group-hover:text-primary transition-colors">
              CampusConnect
            </span>
          </Link>

          <h1 className="font-display text-3xl font-bold text-foreground">
            Welcome back
          </h1>
          <p className="mt-2 text-muted-foreground">
            Sign in to continue exploring campus events
          </p>

          <form onSubmit={handleSubmit} className="mt-8 space-y-5">
            <div>
              <label htmlFor="email" className="block text-sm font-medium text-foreground mb-2">
                Email address
              </label>
              <div className="relative">
                <Mail className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-muted-foreground" />
                <Input
                  id="email"
                  type="email"
                  placeholder="you@university.edu"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="pl-12"
                  required
                />
              </div>
            </div>

            <div>
              <label htmlFor="password" className="block text-sm font-medium text-foreground mb-2">
                Password
              </label>
              <div className="relative">
                <Lock className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-muted-foreground" />
                <Input
                  id="password"
                  type={showPassword ? 'text' : 'password'}
                  placeholder="••••••••"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="pl-12 pr-10"
                  required
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-4 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground transition-colors"
                >
                  {showPassword ? (
                    <EyeOff className="h-5 w-5" />
                  ) : (
                    <Eye className="h-5 w-5" />
                  )}
                </button>
              </div>
              <div className="mt-2 text-right">
                <Link
                  to={{
                    pathname: '/forgot-password',
                    search: email.trim() ? `?email=${encodeURIComponent(email.trim())}` : '',
                  }}
                  className="text-sm font-medium text-primary hover:underline"
                >
                  Forgot password?
                </Link>
              </div>
            </div>

            <div>
              <label htmlFor="captcha" className="block text-sm font-medium text-foreground mb-2">
                Verify you're human
              </label>
              <div className="grid grid-cols-[1fr_auto] gap-2">
                <div className="h-10 px-4 rounded-md border border-border bg-muted/40 flex items-center justify-between">
                  <span className="font-semibold text-foreground tracking-wide">{captcha.question}</span>
                  <button
                    type="button"
                    onClick={refreshCaptcha}
                    className="text-muted-foreground hover:text-foreground transition-colors"
                    aria-label="Refresh captcha"
                  >
                    <RefreshCw className="h-4 w-4" />
                  </button>
                </div>

                <div className="relative">
                  <Input
                    id="captcha"
                    type="text"
                    inputMode="numeric"
                    placeholder="Answer"
                    value={captchaInput}
                    onChange={(e) => setCaptchaInput(e.target.value.replace(/[^0-9-]/g, ''))}
                    className="pr-10"
                    required
                    disabled={!hasCredentials}
                  />
                  {isCaptchaValid && (
                    <CheckCircle2 className="absolute right-3 top-1/2 -translate-y-1/2 h-5 w-5 text-green-600" />
                  )}
                </div>
              </div>
              {!hasCredentials && (
                <p className="text-xs text-muted-foreground mt-2">
                  Enter email and password first to solve captcha.
                </p>
              )}
            </div>

            <Button type="submit" className="w-full" disabled={isLoading || !isCaptchaValid}>
              {isLoading ? 'Signing in...' : 'Sign in'}
              <ArrowRight className="ml-2 h-4 w-4" />
            </Button>
          </form>

          <p className="mt-6 text-center text-sm text-muted-foreground">
            Don't have an account?{' '}
            <Link to="/register" className="font-semibold text-primary hover:underline">
              Sign up
            </Link>
          </p>


        </div>
      </div>

      {/* Right Panel - Decorative */}
      <div className="hidden lg:flex flex-1 gradient-primary items-center justify-center p-12 relative overflow-hidden">
        <div className="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHZpZXdCb3g9IjAgMCA2MCA2MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48ZyBmaWxsPSJub25lIiBmaWxsLXJ1bGU9ImV2ZW5vZGQiPjxnIGZpbGw9IiNmZmYiIGZpbGwtb3BhY2l0eT0iMC4xIj48cGF0aCBkPSJNMzYgMzRjMC0yLjIxLTEuNzktNC00LTRzLTQgMS43OS00IDQgMS43OSA0IDQgNCA0LTEuNzkgNC00eiIvPjwvZz48L2c+PC9zdmc+')] opacity-30" />
        
        <div className="relative text-center text-primary-foreground">
          <h2 className="font-display text-4xl font-bold mb-4">
            Welcome back to CampusConnect
          </h2>
          <p className="text-primary-foreground/90 max-w-md text-lg">
            Discover, attend, and create amazing events on your campus.
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;
