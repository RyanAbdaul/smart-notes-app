const Buttons = ({ icon, value, count, onClick, className }) => {
  return (
    <div className="w-full flex items-center justify-around">
      <button
        type="button"
        onClick={onClick}
        className={` w-full text-left p-3 rounded-2xl font-medium text-2xl text-zinc-500
        hover:cursor-pointer   hover:text-zinc-800
        flex items-center justify-between gap-2 
        ${!className? "hover:bg-[#EAE7EA]":className}
        `}
      >
        {/* القسم الأيمن: الأيقونة والنص */}
        <div className="flex items-center gap-1">
          {icon && icon}
          {value && <span>{value}</span>}
        </div>
        {/* القسم الأيسر: العدد (لو موجود يظهر) */}
        {count !== undefined && (
          <span className="text-sm bg-zinc-200 px-2 py-0.5 rounded-full text-zinc-600">
            {count}
          </span>
        )}
      </button>
    </div>
  );
};

export default Buttons;
