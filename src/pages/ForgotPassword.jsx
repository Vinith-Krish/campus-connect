import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { authService } from '../services/authService';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Calendar, Mail, ArrowLeft, CheckCircle } from 'lucide-react';
import { useToast } from '../hooks/use-toast';

const ForgotPassword = () => {
  const [email, setEmail] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [isSuccess, setIsSuccess] = useState(false);
  const { toast } = useToast();

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!email.trim()) {
      toast({
        title: 'Validation Error',
        description: 'Please enter your email address',
        variant: 'destructive',
      });
      return;
    }

    setIsLoading(true);
    try {
      await authService.forgotPassword(email);
      setIsSuccess(true);
      toast({
        title: 'Email Sent!',
        description: 'Check your email for password reset instructions.',
      });
    } catch (error) {
      toast({
        title: 'Request Failed',
        description: error.response?.data?.message || 'Failed to send reset email. Please try again.',
        variant: 'destructive',
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-background flex">
      {/* Left Panel - Form */}
      <div className="flex-1 flex items-center justify-center p-8">
        <div className="w-full max-w-md animate-slide-up">
          <Link to="/login" className="inline-flex items-center gap-2 text-muted-foreground hover:text-foreground transition-colors mb-8">
            <ArrowLeft className="h-4 w-4" />
            Back to login
          </Link>

          <Link to="/" className="inline-flex items-center gap-2 mb-8 group">
            <div className="p-2 rounded-xl gradient-primary">
              <Calendar className="h-5 w-5 text-primary-foreground" />
            </div>
            <span className="font-display text-xl font-bold text-foreground group-hover:text-primary transition-colors">
              CampusConnect
            </span>
          </Link>

          {!isSuccess ? (
            <>
              <h1 className="font-display text-3xl font-bold text-foreground">
                Forgot your password?
              </h1>
              <p className="mt-2 text-muted-foreground">
                No worries! Enter your email and we'll send you reset instructions.
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

                <Button type="submit" variant="hero" className="w-full" size="lg" disabled={isLoading}>
                  {isLoading ? (
                    <div className="animate-spin rounded-full h-5 w-5 border-2 border-primary-foreground border-t-transparent" />
                  ) : (
                    'Send Reset Link'
                  )}
                </Button>
              </form>
            </>
          ) : (
            <div className="text-center">
              <div className="w-16 h-16 rounded-full bg-green-500/10 flex items-center justify-center mx-auto mb-6">
                <CheckCircle className="h-8 w-8 text-green-500" />
              </div>
              <h1 className="font-display text-3xl font-bold text-foreground mb-2">
                Check your email
              </h1>
              <p className="text-muted-foreground mb-8">
                We've sent password reset instructions to <span className="font-semibold text-foreground">{email}</span>
              </p>
              <Button variant="outline" asChild className="w-full">
                <Link to="/login">Back to Login</Link>
              </Button>
            </div>
          )}
        </div>
      </div>

      {/* Right Panel - Decorative */}
      <div className="hidden lg:flex flex-1 gradient-primary items-center justify-center p-12 relative overflow-hidden">
        <div className="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHZpZXdCb3g9IjAgMCA2MCA2MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48ZyBmaWxsPSJub25lIiBmaWxsLXJ1bGU9ImV2ZW5vZGQiPjxnIGZpbGw9IiNmZmYiIGZpbGwtb3BhY2l0eT0iMC4xIj48cGF0aCBkPSJNMzYgMzRjMC0yLjIxLTEuNzktNC00LTRzLTQgMS43OS00IDQgMS43OSA0IDQgNCA0LTEuNzkgNC00eiIvPjwvZz48L2c+PC9zdmc+')] opacity-30" />
        
        <div className="relative text-center text-primary-foreground">
          <div className="w-24 h-24 rounded-2xl bg-primary-foreground/20 backdrop-blur flex items-center justify-center mx-auto mb-8 animate-float">
            <Mail className="h-12 w-12" />
          </div>
          <h2 className="font-display text-4xl font-bold mb-4">
            Password Recovery
          </h2>
          <p className="text-primary-foreground/80 max-w-md text-lg">
            We'll help you reset your password and get you back to discovering amazing campus events.
          </p>
        </div>
      </div>
    </div>
  );
};

export default ForgotPassword;
