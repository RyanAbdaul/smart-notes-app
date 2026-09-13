const Inputs = ({ label, id, type, name, value, onChange }) => {
  return (
    <div className="space-y-2">
      <label htmlFor={id} className="block text-sm font-semibold text-zinc-700">
        {label}
      </label>
      <input
        id={id}
        type={type}
        name={name}
        value={value}
        onChange={onChange}
        required
        className="w-full rounded-xl border border-zinc-300 bg-white px-4 py-3 text-zinc-900 outline-none transition placeholder:text-zinc-400 focus:border-amber-600 focus:ring-4 focus:ring-amber-100"
      />
    </div>
  );
};

export default Inputs;
