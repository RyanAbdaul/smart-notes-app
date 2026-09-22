import Spinner from "../../utils/Spinner";

const Buttons = ({ icon, value, count, onClick, className,loading }) => {
  return (
    <div className="w-full flex items-center justify-around">
      <button
       disabled={loading}
        type="button"
        onClick={onClick}
        className={` w-full text-left p-3 rounded-2xl font-medium text-2xl text-zinc-500
        hover:cursor-pointer   hover:text-zinc-800
        flex items-center justify-between gap-2 
        ${!className? "hover:bg-[#EAE7EA]":className}
        `}
      >
       <div className="flex items-center gap-1">
          {loading ? (
            <Spinner />
          ) : (
            icon && icon
          )}
          {value && <span>{value}</span>}
        </div>
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
