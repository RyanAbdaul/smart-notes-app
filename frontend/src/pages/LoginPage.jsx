import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Inputs from "../components/common/Inputs";
import { useDispatch, useSelector } from "react-redux";
import { userLogin } from "../features/auth/authService";
import ErrorHandler from "../utils/ErrorHandler";

const LoginPage = () => {
  const [formData, setFormData] = useState({ email: "", password: "" });
  const { error, status } = useSelector((state) => state.auth);
  const navigate = useNavigate();
  const isLoading = status === "loading";
  const dispatch = useDispatch();
  const handleChange = (event) => {
    const { name, value } = event.target;
    setFormData((currentData) => ({ ...currentData, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    try {
      await dispatch(userLogin(formData)).unwrap();

      navigate("/dashboard");
    } catch (error) {
      console.log(error);
    }
  };

  return (
    <main className="flex min-h-screen items-center justify-center bg-[#F6F3F5] px-4 py-10">
      <section className="w-full max-w-md rounded-3xl bg-white p-8 shadow-xl shadow-zinc-200/70 sm:p-10">
        <div className="mb-8 text-center">
          <h1 className="text-3xl font-bold text-zinc-800">Welcome back</h1>
          <p className="mt-2 text-zinc-500">
            Sign in to continue to Smart Notes.
          </p>
        </div>

        <form className="space-y-5" onSubmit={handleSubmit}>
          <Inputs
            label="Email address"
            id="email"
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
          />
          <Inputs
            label="Password"
            id="password"
            type="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
          />

          {error && <ErrorHandler error={error} />}
          <button
            type="submit"
            className="w-full rounded-xl bg-amber-600 px-4 py-3 font-semibold text-white transition hover:bg-amber-700 focus:outline-none focus:ring-4 focus:ring-amber-200"
            disabled={isLoading}
          >
            {isLoading ? "Loading..." : "Sign in"}
          </button>
        </form>

        <p className="mt-6 text-center text-sm text-zinc-500">
          Don&apos;t have an account?{" "}
          <Link
            to="/register"
            className="font-semibold text-amber-700 hover:text-amber-800"
          >
            Create one
          </Link>
        </p>
      </section>
    </main>
  );
};

export default LoginPage;
