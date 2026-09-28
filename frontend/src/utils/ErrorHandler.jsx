
const ErrorHandler = ({error}) => {
  return (
    <div className="my-2 p-2 text-center bg-red-400 font-bold text-white rounded-2xl">
 {error}!
    </div>
  );
};

export default ErrorHandler;
