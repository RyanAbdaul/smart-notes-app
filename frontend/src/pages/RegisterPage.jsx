import { useState } from "react";
import { Link } from "react-router-dom";
import Inputs from "../components/common/Inputs";

const RegisterPage = () => {
  const [formData, setFormData] = useState({
    fName: "",
    lName: "",
    email: "",
    password: "",
  });

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFormData((currentData) => ({ ...currentData, [name]: value }));
  };

  const handleSubmit = (event) => {
    event.preventDefault();
  };

  return (
    <main className="flex min-h-screen items-center justify-center bg-[#F6F3F5] px-4 py-10">
      <section className="w-full max-w-md rounded-3xl bg-white p-8 shadow-xl shadow-zinc-200/70 sm:p-10">
        <div className="mb-8 text-center">
          <h1 className="text-3xl font-bold text-zinc-800">Create your account</h1>
          <p className="mt-2 text-zinc-500">Start organizing your notes in one place.</p>
        </div>

        <form className="space-y-5" onSubmit={handleSubmit}>
          <Inputs
            label="First name"
            id="fName"
            type="text"
            name="fName"
            value={formData.fName}
            onChange={handleChange}
          />
          <Inputs
            label="Last name"
            id="lName"
            type="text"
            name="lName"
            value={formData.lName}
            onChange={handleChange}
          />
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

          <button
            type="submit"
            className="w-full rounded-xl bg-amber-600 px-4 py-3 font-semibold text-white transition hover:bg-amber-700 focus:outline-none focus:ring-4 focus:ring-amber-200"
          >
            Create account
          </button>
        </form>

        <p className="mt-6 text-center text-sm text-zinc-500">
          Already have an account?{" "}
          <Link to="/login" className="font-semibold text-amber-700 hover:text-amber-800">
            Sign in
          </Link>
        </p>
      </section>
    </main>
  );
};

export default RegisterPage;
