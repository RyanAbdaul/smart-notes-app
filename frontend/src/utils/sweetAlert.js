import Swal from 'sweetalert2';
import withReactContent from 'sweetalert2-react-content';

const MySwal = withReactContent(Swal);

export const showSuccessAlert = (title = "Success!") => {
  return MySwal.fire({
    icon: 'success',
    title: title,
    timer: 1500,
    showConfirmButton: false,
  });
};

export const showErrorAlert = (message = "Something went wrong!") => {
  return MySwal.fire({
    icon: 'error',
    title: 'Oops...',
    text: message,
    confirmButtonColor: '#d33',
  });
};

export const showConfirmDelete = async () => {
  const result = await MySwal.fire({
    title: 'Are you sure?',
    text: "You won't be able to revert this!",
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#f36703', // لون متناسق مع تصميم الـ Spinner الخاص بك
    cancelButtonColor: '#6b7280',
    confirmButtonText: 'Yes, delete it!',
  });
  
  return result.isConfirmed;
};