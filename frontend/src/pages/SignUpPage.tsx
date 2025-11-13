import { SignupForm } from "@/components/auth/signup-form"
import React from "react";

const SignUpPage = () => {
  return (
    <div className="bg-muted flex min-h-svh flex-col items-center justify-center p-6 md:p-10">
      <div className="container max-w-4xl">
        <SignupForm />
      </div>
    </div>
  );
};

export default SignUpPage;

